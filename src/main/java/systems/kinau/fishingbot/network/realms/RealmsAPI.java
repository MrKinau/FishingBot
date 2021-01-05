/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/2
 */

package systems.kinau.fishingbot.network.realms;

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
import java.util.ArrayList;
import java.util.List;

public class RealmsAPI {

    private final String REALMS_ENDPOINT = "https://pc.realms.minecraft.net";

    private HttpClient client;

    public RealmsAPI(AuthData authData) {
        BasicCookieStore cookies = new BasicCookieStore();

        BasicClientCookie sidCookie = new BasicClientCookie("sid", String.join(":", "token", authData.getAccessToken(), authData.getProfile()));
        BasicClientCookie userCookie = new BasicClientCookie("user", authData.getUsername());
        BasicClientCookie versionCookie = new BasicClientCookie("version", ProtocolConstants.getVersionString(FishingBot.getInstance().getCurrentBot().getServerProtocol()));

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

    public List<Realm> getPossibleWorlds() {
        List<Realm> joinableRealms = new ArrayList<>();
        HttpUriRequest request = RequestBuilder.get()
                .setUri(REALMS_ENDPOINT + "/worlds")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                FishingBot.getI18n().severe("realms-could-not-connect-to-endpoint", REALMS_ENDPOINT, answer.getStatusLine().toString());
                return joinableRealms;
            }
            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            JSONArray servers = (JSONArray) responseJson.get("servers");
            if (servers.size() == 0)
                return joinableRealms;

            servers.forEach(server -> {
                long id = (long) ((JSONObject) server).get("id");
                String owner = (String) ((JSONObject) server).get("owner");
                String name = (String) ((JSONObject) server).get("name");
                String motd = (String) ((JSONObject) server).get("motd");
                joinableRealms.add(new Realm(id, name, owner, motd));
            });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            FishingBot.getI18n().severe("realms-could-not-connect-to-endpoint", REALMS_ENDPOINT, e.getMessage());
        }
        return joinableRealms;
    }
    
    public void printRealms(List<Realm> realms) {
        if (realms.isEmpty()) {
            FishingBot.getI18n().info("realms-no-realms");
            return;
        }
        realms.forEach(realm -> {
            FishingBot.getLog().info("");
            FishingBot.getLog().info("ID: " + realm.getId());
            FishingBot.getLog().info("name: " + realm.getName());
            FishingBot.getLog().info("motd: " + realm.getMotd());
            FishingBot.getLog().info("owner: " + realm.getOwner());
        });
    }

    public void agreeTos() {
        HttpUriRequest request = RequestBuilder.post()
                .setUri(REALMS_ENDPOINT + "/mco/tos/agreed")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 204) {
                FishingBot.getI18n().severe("realms-could-not-accept-tos", answer.getStatusLine());
                return;
            } else
                FishingBot.getI18n().info("realms-accepted-tos");
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
                FishingBot.getI18n().severe("realms-could-not-determine-address", REALMS_ENDPOINT, answer.getStatusLine());
                return null;
            }
            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            FishingBot.getI18n().info("realms-connecting-to", responseJson.toJSONString());
            return (String) responseJson.get("address");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            FishingBot.getI18n().severe("realms-could-not-connect-to-endpoint", REALMS_ENDPOINT, e.getMessage());
        }
        return null;
    }
}
