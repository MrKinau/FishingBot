/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import systems.kinau.fishingbot.FishingBot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@AllArgsConstructor
public class Authenticator {

    private final String AUTH_SERVER = "https://authserver.mojang.com/authenticate";

    private String username;
    private String password;

    public AuthData authenticate() {
        try {
            URL authUrl = new URL(AUTH_SERVER);

            HttpURLConnection conn = (HttpURLConnection) authUrl.openConnection();
            conn.setRequestProperty("content-type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            JsonObject data = new JsonObject();
            JsonObject agent = new JsonObject();
            agent.addProperty("name", "minecraft");
            agent.addProperty("version", "1");
            data.add("agent", agent);
            data.addProperty("username", username);
            data.addProperty("password", password);

            FishingBot.getLog().info("Try to authenticate " + username + ":" + password.replaceAll(".", "*"));

            wr.write(data.toString());
            wr.flush();

            try {
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream stream = conn.getInputStream();
                    JsonObject answer = (JsonObject) new JsonParser().parse(convertStreamToString(stream));
                    String accessToken = answer.get("accessToken").getAsString();
                    String clientToken = answer.get("clientToken").getAsString();
                    String profile = answer.getAsJsonObject("selectedProfile").get("id").getAsString();
                    String username = answer.getAsJsonObject("selectedProfile").get("name").getAsString();
                    FishingBot.getLog().info("Authentication succeeded: " + answer.toString());
                    return new AuthData(accessToken, clientToken, profile, username);
                } else {
                    String error = convertStreamToString(conn.getErrorStream());
                    FishingBot.getLog().warning("Authentication failed: " + error);
                }

            } catch (IOException e) {
                FishingBot.getLog().severe("Could not open connection to: " + AUTH_SERVER);
            } catch (JsonSyntaxException e) {
                FishingBot.getLog().severe("Could not parse answer from: " + AUTH_SERVER);
            }
        } catch (MalformedURLException ex) {
            FishingBot.getLog().severe(AUTH_SERVER + " is a malformed URL! Does not make any sense!");
        } catch (IOException ex) {
            FishingBot.getLog().severe("Could not open connection to: " + AUTH_SERVER);
        }
        return null;
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
