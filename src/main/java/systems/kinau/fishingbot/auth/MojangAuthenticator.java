/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.auth;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MojangAuthenticator implements IAuthenticator {

    private final String AUTH_ENDPOINT = "https://authserver.mojang.com";

    private final HttpClient client;
    private final File accountFile;

    public MojangAuthenticator(File accountFile) {
        this.client = HttpClientBuilder.create().build();
        this.accountFile = accountFile;
    }

    public Optional<AuthData> authenticateWithTokens(String accessToken, String clientToken, String accountName, String profileId) {
        JSONObject validateRequest = new JSONObject();
        validateRequest.put("accessToken", accessToken);
        validateRequest.put("clientToken", clientToken);

        try {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri(AUTH_ENDPOINT + "/validate")
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .setEntity(new StringEntity(validateRequest.toString()))
                    .build();
            FishingBot.getI18n().info("auth-using-access-token");
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() == 403) {
                JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
                FishingBot.getI18n().warning("auth-invalid-access-token", responseJson.get("errorMessage"));
                return refresh(accessToken, clientToken);
            } else if (answer.getStatusLine().getStatusCode() == 204) {
                FishingBot.getI18n().info("auth-successful");
                return Optional.of(new AuthData(accessToken, clientToken, profileId, accountName));
            } else {
                FishingBot.getI18n().warning("auth-access-token-irrecoverably");
                return Optional.empty();
            }
        } catch (IOException | ParseException e) {
            FishingBot.getI18n().severe("auth-could-not-connect", AUTH_ENDPOINT + "/validate");
        }
        return Optional.empty();
    }

    private Optional<AuthData> refresh(String accessToken, String clientToken) {
        JSONObject validateRequest = new JSONObject();
        validateRequest.put("accessToken", accessToken);
        validateRequest.put("clientToken", clientToken);

        try {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri(AUTH_ENDPOINT + "/refresh")
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .setEntity(new StringEntity(validateRequest.toString()))
                    .build();

            FishingBot.getI18n().info("auth-try-refreshing-access-token");
            HttpResponse answer = client.execute(request);

            if (answer.getStatusLine().getStatusCode() == 200) {
                JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
                String newAccessToken = (String) responseJson.get("accessToken");
                String newUserName = (String) ((JSONObject)responseJson.get("selectedProfile")).get("name");
                String newUUID = (String) ((JSONObject)responseJson.get("selectedProfile")).get("id");
                FishingBot.getI18n().info("auth-successful");
                return Optional.of(new AuthData(newAccessToken, clientToken, newUUID, newUserName));
            } else {
                FishingBot.getI18n().warning("auth-access-token-irrecoverably");
                return Optional.empty();
            }
        } catch (IOException | ParseException e) {
            FishingBot.getI18n().severe("auth-could-not-connect", AUTH_ENDPOINT + "/validate");
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthData> authenticate(String loginName, String password) {
        JSONObject data = new JSONObject();
        JSONObject agent = new JSONObject();
        agent.put("name", "minecraft");
        agent.put("version", "1");
        data.put("agent", agent);
        data.put("username", loginName);
        data.put("password", password);

        try {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri(AUTH_ENDPOINT + "/authenticate")
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .setEntity(new StringEntity(data.toString()))
                    .build();

            HttpResponse answer = client.execute(request);

            FishingBot.getI18n().info("auth-using-password", StringUtils.maskUsername(loginName), AuthService.MOJANG);

            if (answer.getStatusLine().getStatusCode() != 200) {
                FishingBot.getI18n().severe("auth-failed", answer.getStatusLine().getStatusCode(), AUTH_ENDPOINT, answer.getStatusLine());
                FishingBot.getLog().severe(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
                return Optional.empty();
            }

            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            String accessToken = (String) responseJson.get("accessToken");
            String clientToken = (String) responseJson.get("clientToken");
            String profile = (String) ((JSONObject)responseJson.get("selectedProfile")).get("id");
            String username = (String) ((JSONObject)responseJson.get("selectedProfile")).get("name");
            FishingBot.getI18n().info("auth-successful");
            return Optional.of(new AuthData(accessToken, clientToken, profile, username));
        } catch (IOException | ParseException e) {
            FishingBot.getI18n().severe("auth-could-not-connect", AUTH_ENDPOINT + "/validate");
        }
        return Optional.empty();
    }
}
