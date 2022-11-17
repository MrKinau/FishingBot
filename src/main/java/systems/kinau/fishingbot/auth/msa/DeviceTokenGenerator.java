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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created: 22.09.2021
 *
 * @author Summerfeeling
 */
public class DeviceTokenGenerator {

    private static final HttpClient CLIENT = HttpClientBuilder.create().build();

    public static DeviceTokenCallback createDeviceToken(String clientId) {
        HttpUriRequest request = RequestBuilder.post()
            .setUri("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode")
            .setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .addParameter("client_id", clientId)
            .addParameter("scope", "XboxLive.signin offline_access")
            .build();

        try {
            HttpResponse response = CLIENT.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                EntityUtils.consumeQuietly(response.getEntity());
                throw new RuntimeException("Could not request device token: " + response.getStatusLine().getReasonPhrase());
            }

            JsonObject object = new JsonParser().parse(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)).getAsJsonObject();

            return new DeviceTokenCallback(
                object.get("user_code").getAsString(),
                object.get("device_code").getAsString(),
                object.get("verification_uri").getAsString(),
                object.get("expires_in").getAsInt(),
                object.get("interval").getAsInt()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
