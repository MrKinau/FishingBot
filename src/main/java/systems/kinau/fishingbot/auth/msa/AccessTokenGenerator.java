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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created: 22.09.2021
 *
 * @author Summerfeeling
 */
public class AccessTokenGenerator {

    private static final HttpClient CLIENT = HttpClientBuilder.create().build();

    public static AccessTokenCallback createAccessToken(String refreshToken, String clientId) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("User does not have a refresh token");
        }

        FishingBot.getI18n().info("auth-try-refreshing-access-token");

        HttpUriRequest request = RequestBuilder.post()
            .setUri("https://login.microsoftonline.com/consumers/oauth2/v2.0/token")
            .setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .addParameter("client_id", clientId)
            .addParameter("grant_type", "refresh_token")
            .addParameter("refresh_token", refreshToken)
            .build();

        try {
            HttpResponse response = CLIENT.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                EntityUtils.consumeQuietly(response.getEntity());
                throw new RuntimeException("Could not request access token: " + response.getStatusLine().getReasonPhrase());
            }

            JsonObject object = new JsonParser().parse(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)).getAsJsonObject();

            if (object.has("error")) {
                throw new RuntimeException(object.get("error").getAsString().split("\r")[0]);
            }

            FishingBot.getI18n().info("auth-refreshed-access-token");

            return new AccessTokenCallback(
                object.get("access_token").getAsString(),
                object.get("refresh_token").getAsString()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
