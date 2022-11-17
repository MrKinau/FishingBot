package systems.kinau.fishingbot.auth.msa;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.Pair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created: 22.09.2021
 *
 * @author Summerfeeling
 */
public class RefreshTokenCallback {

    private static final HttpClient CLIENT = HttpClientBuilder.create().build();

    public static String await(DeviceTokenCallback callback, String clientId) throws ObtainTokenException {
        AtomicReference<Pair<RefreshTokenResult, String>> refreshToken = new AtomicReference<>(get(callback, clientId));

        while (refreshToken.get().getKey() == RefreshTokenResult.AUTHORIZATION_PENDING) {
            FishingBot.getLog().info(refreshToken.get().getValue());
            refreshToken.set(get(callback, clientId));

            if (FishingBot.getInstance().getCurrentBot().isPreventStartup())
                break;

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(callback.getInterval()));
            } catch (InterruptedException e) {
                break;
            }
        }

        Pair<RefreshTokenResult, String> pair = refreshToken.get();

        if (pair.getKey() == RefreshTokenResult.AUTHORIZATION_SUCCEEDED) {
            FishingBot.getI18n().info("auth-authorization-succeeded");
            return pair.getValue();
        }

        if (pair.getKey() == RefreshTokenResult.AUTHORIZATION_DECLINED || pair.getKey() == RefreshTokenResult.EXPIRED_TOKEN) {
            throw new ObtainTokenException(pair.getKey());
        } else {
            return null;
        }
    }

    private static Pair<RefreshTokenResult, String> get(DeviceTokenCallback callback, String clientId) {
        HttpUriRequest request = RequestBuilder.post()
            .setUri("https://login.microsoftonline.com/consumers/oauth2/v2.0/token")
            .setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .addParameter("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
            .addParameter("client_id", clientId)
            .addParameter("device_code", callback.getDeviceCode())
            .build();

        try {
            HttpResponse response = CLIENT.execute(request);
            JsonObject object = new JsonParser().parse(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)).getAsJsonObject();

            // Token has not been generated
            if (response.getStatusLine().getStatusCode() == 400 && object.has("error")) {
                String errorMessage = object.get("error_description").getAsString();
                errorMessage = errorMessage.split("\r")[0];

                return Pair.of(RefreshTokenResult.valueOf(object.get("error").getAsString().toUpperCase()), errorMessage);
            }

            return Pair.of(RefreshTokenResult.AUTHORIZATION_SUCCEEDED, object.get("refresh_token").getAsString());
        } catch (IOException e) {
            FishingBot.getLog().warning("Error while waiting for access token: " + e.getMessage());
            return Pair.of(RefreshTokenResult.AUTHORIZATION_PENDING, e.getLocalizedMessage());
        }
    }

}
