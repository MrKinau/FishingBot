/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/2
 */

package systems.kinau.fishingbot.realms;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.IOException;

public class RealmsAPI {

    private final String REALMS_ENDPOINT = "https://pc.realms.minecraft.net";

    private HttpClient client;

    public RealmsAPI(AuthData authData) {
        BasicCookieStore cookies = new BasicCookieStore();

        BasicClientCookie sidCookie = new BasicClientCookie("sid", String.join(":", "token", authData.getAccessToken(), authData.getProfile()));
        BasicClientCookie userCookie = new BasicClientCookie("user", authData.getUsername());
        BasicClientCookie versionCookie = new BasicClientCookie("version", ProtocolConstants.getVersionString(FishingBot.getInstance().getServerProtocol()));

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
                FishingBot.getLog().severe("Could not connect to " + REALMS_ENDPOINT + ": " + answer.getStatusLine());
                return;
            }
            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            JSONArray servers = (JSONArray) responseJson.get("servers");
            if (servers.size() == 0) {
                FishingBot.getLog().warning("There are no possible realms this account can join");
                return;
            }
            FishingBot.getLog().info("Possible realms to join:");
            servers.forEach(server -> {
                long id = (long) ((JSONObject)server).get("id");
                String owner = (String) ((JSONObject)server).get("owner");
                String name = (String) ((JSONObject)server).get("name");
                String motd = (String) ((JSONObject)server).get("motd");
                FishingBot.getLog().info("ID: " + id);
                FishingBot.getLog().info("name: " + name);
                FishingBot.getLog().info("motd: " + motd);
                FishingBot.getLog().info("owner: " + owner);
                FishingBot.getLog().info("");
            });
        } catch (IOException | ParseException e) {
            FishingBot.getLog().severe("Could not connect to " + REALMS_ENDPOINT);
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
                FishingBot.getLog().severe("Could not accept TOS: " + answer.getStatusLine());
                return;
            } else
                FishingBot.getLog().info("Accepted TOS!");
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
                FishingBot.getLog().severe("Could not retrieve IP from " + REALMS_ENDPOINT + ": " + answer.getStatusLine());
                return null;
            }
            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            FishingBot.getLog().info("Connecting to: " + responseJson.toString());
            return (String) responseJson.get("address");
        } catch (IOException | ParseException e) {
            FishingBot.getLog().severe("Could not connect to " + REALMS_ENDPOINT);
        }
        return null;
    }
}
