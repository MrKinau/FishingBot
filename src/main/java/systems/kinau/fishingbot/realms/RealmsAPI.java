/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/2
 */

package systems.kinau.fishingbot.realms;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.IOException;

public class RealmsAPI {

    private static final String REALMS_ENDPOINT = "https://pc.realms.minecraft.net";

    private AuthData authData;
    private BasicCookieStore cookies;
    private HttpClient client;

    public RealmsAPI(AuthData authData) {
        this.authData = authData;
        this.cookies = new BasicCookieStore();

        BasicClientCookie sidCookie = new BasicClientCookie("sid", String.join(":", "token", authData.getAccessToken(), authData.getProfile()));
        BasicClientCookie userCookie = new BasicClientCookie("user", authData.getUsername());
        BasicClientCookie versionCookie = new BasicClientCookie("version", ProtocolConstants.getVersionString(MineBot.getServerProtocol()));

        sidCookie.setDomain(".pc.realms.minecraft.net");
        userCookie.setDomain(".pc.realms.minecraft.net");
        versionCookie.setDomain(".pc.realms.minecraft.net");

        sidCookie.setPath("/");
        userCookie.setPath("/");
        versionCookie.setPath("/");

        cookies.addCookie(sidCookie);
        cookies.addCookie(userCookie);
        cookies.addCookie(versionCookie);

        client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookies)
                .build();
    }

    public void printPossibleWorlds() {
        HttpUriRequest request = RequestBuilder.get()
                .setUri(REALMS_ENDPOINT + "/worlds")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                MineBot.getLog().severe("Could not connect to " + REALMS_ENDPOINT + ": " + answer.getStatusLine());
                return;
            }
            JsonObject responseJson = (JsonObject) new JsonParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            JsonArray servers = responseJson.getAsJsonArray("servers");
            if (servers.size() == 0) {
                MineBot.getLog().warning("There are no possible realms this account can join");
                return;
            }
            MineBot.getLog().info("Possible realms to join:");
            servers.forEach(server -> {
                long id = server.getAsJsonObject().get("id").getAsLong();
                String owner = server.getAsJsonObject().get("owner").getAsString();
                String name = server.getAsJsonObject().get("name").getAsString();
                String motd = server.getAsJsonObject().get("motd").getAsString();
                MineBot.getLog().info("ID: " + id);
                MineBot.getLog().info("name: " + name);
                MineBot.getLog().info("motd: " + motd);
                MineBot.getLog().info("owner: " + owner);
                MineBot.getLog().info("");
            });
        } catch (IOException e) {
            MineBot.getLog().severe("Could not connect to " + REALMS_ENDPOINT);
        }
    }

    public void agreeTos() {
        HttpUriRequest request = RequestBuilder.post()
                .setUri(REALMS_ENDPOINT + "/mco/tos/agreed")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 204) {
                MineBot.getLog().severe("Could not accept TOS: " + answer.getStatusLine());
                return;
            } else
                MineBot.getLog().info("Accepted TOS!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerIP(long serverId) {
        HttpUriRequest request = RequestBuilder.get()
                .setUri(REALMS_ENDPOINT + "/worlds/v1/" + serverId + "/join/pc")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                MineBot.getLog().severe("Could not retrieve IP from " + REALMS_ENDPOINT + ": " + answer.getStatusLine());
                return null;
            }
            JsonObject responseJson = (JsonObject) new JsonParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            MineBot.getLog().info("Connecting to: " + responseJson.toString());
            return responseJson.get("address").getAsString();
        } catch (IOException e) {
            MineBot.getLog().severe("Could not connect to " + REALMS_ENDPOINT);
        }
        return null;
    }
}
