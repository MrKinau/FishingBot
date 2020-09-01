/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.auth;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import systems.kinau.fishingbot.FishingBot;

import java.io.IOException;

@AllArgsConstructor
public class Authenticator {

    private final String AUTH_SERVER = "https://authserver.mojang.com/authenticate";

    private String username;
    private String password;

    public AuthData authenticate() {
        JSONObject data = new JSONObject();
        JSONObject agent = new JSONObject();
        agent.put("name", "minecraft");
        agent.put("version", "1");
        data.put("agent", agent);
        data.put("username", username);
        data.put("password", password);

        try {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri(AUTH_SERVER)
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .setEntity(new StringEntity(data.toString()))
                    .build();

            HttpResponse answer = HttpClientBuilder.create().build().execute(request);
            FishingBot.getLog().info("Try to authenticate " + username + ":" + password.replaceAll(".", "*"));

            if (answer.getStatusLine().getStatusCode() != 200) {
                FishingBot.getLog().severe("Authentication failed with code " + answer.getStatusLine().getStatusCode() + " from " + AUTH_SERVER + ": " + answer.getStatusLine());
                FishingBot.getLog().severe(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
                return null;
            }

            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            String accessToken = (String) responseJson.get("accessToken");
            String clientToken = (String) responseJson.get("clientToken");
            String profile = (String) ((JSONObject)responseJson.get("selectedProfile")).get("id");
            String username = (String) ((JSONObject)responseJson.get("selectedProfile")).get("name");
            FishingBot.getLog().info("Authentication successful!");
            return new AuthData(accessToken, clientToken, profile, username);
        } catch (IOException | ParseException e) {
            FishingBot.getLog().severe("Error while connecting to: " + AUTH_SERVER);
        }
        return null;
    }
}
