/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/2
 */

package systems.kinau.fishingbot.network.mojangapi;

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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class MojangAPI {

    private final String REALMS_ENDPOINT = "https://pc.realms.minecraft.net";
    private final String MOJANG_ENDPOINT = "https://api.minecraftservices.com";

    private final HttpClient client;
    private final AuthData authData;

    public MojangAPI(AuthData authData, int protocolId) {
        this.authData = authData;
        BasicCookieStore cookies = new BasicCookieStore();

        BasicClientCookie sidCookie = new BasicClientCookie("sid", String.join(":", "token", authData.getAccessToken(), authData.getUUIDWithoutDashes()));
        BasicClientCookie userCookie = new BasicClientCookie("user", authData.getUsername());
        BasicClientCookie versionCookie = new BasicClientCookie("version", ProtocolConstants.getExactVersionString(protocolId));

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

    public void obtainCertificates() {
        HttpUriRequest request = RequestBuilder.post()
                .setUri(MOJANG_ENDPOINT + "/player/certificates")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authData.getAccessToken())
                .setHeader(HttpHeaders.CONTENT_LENGTH, "0")
                .build();

        try {
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() != 200) {
                EntityUtils.consumeQuietly(answer.getEntity());
                FishingBot.getI18n().severe("could-not-get-keys", MOJANG_ENDPOINT, answer.getStatusLine().toString());
                return;
            }
            JSONObject responseJson = (JSONObject) new JSONParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));

            if (responseJson == null || !responseJson.containsKey("keyPair")) {
                FishingBot.getI18n().severe("could-not-get-keys", MOJANG_ENDPOINT, answer.getStatusLine().toString());
                return;
            }

            JSONObject keyPair = (JSONObject) responseJson.get("keyPair");
            String pubKeyContent = keyPair.get("publicKey").toString()
                    .replace("\n", "")
                    .replace("\\n", "")
                    .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replace("-----END RSA PUBLIC KEY-----", "");
            String privKeyContent = keyPair.get("privateKey").toString()
                    .replace("\n", "")
                    .replace("\\n", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "");
            String pubKeySig = responseJson.get(
                    FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_19_1
                            ? "publicKeySignatureV2"
                            : "publicKeySignature").toString();

            KeyFactory kf = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privKeyContent));
            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKeyContent));
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

            String expirationContent = responseJson.get("expiresAt").toString();
            TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(expirationContent);
            Instant instant = Instant.from(temporalAccessor);
            Date expiresAt = Date.from(instant);

            if (privKey == null || pubKey == null) {
                FishingBot.getI18n().severe("could-not-get-keys", MOJANG_ENDPOINT, "private key or public key null");
                return;
            }

            authData.setProfileKeys(new AuthData.ProfileKeys(pubKey, pubKeySig, privKey, expiresAt.getTime()));

            FishingBot.getI18n().info("retrieved-keys");
        } catch (IOException | ParseException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            FishingBot.getI18n().severe("could-not-get-keys", MOJANG_ENDPOINT, e.getMessage());
        }
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
                EntityUtils.consumeQuietly(answer.getEntity());
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
            EntityUtils.consumeQuietly(answer.getEntity());
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
                EntityUtils.consumeQuietly(answer.getEntity());
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
