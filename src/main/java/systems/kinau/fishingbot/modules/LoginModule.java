/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.login.EncryptionRequestEvent;
import systems.kinau.fishingbot.event.login.LoginDisconnectEvent;
import systems.kinau.fishingbot.event.login.LoginSuccessEvent;
import systems.kinau.fishingbot.event.login.SetCompressionEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.State;
import systems.kinau.fishingbot.network.protocol.login.PacketOutEncryptionResponse;
import systems.kinau.fishingbot.network.protocol.login.PacketOutLoginStart;
import systems.kinau.fishingbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;

public class LoginModule extends Module implements Listener {

    @Getter private String userName;

    public LoginModule(String userName) {
        this.userName = userName;
        MineBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onEnable() {
        MineBot.getInstance().getNet().sendPacket(new PacketOutLoginStart(getUserName()));
    }

    @Override
    public void onDisable() {
        MineBot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }

    @EventHandler
    public void onEncryptionRequest(EncryptionRequestEvent event) {
        NetworkHandler networkHandler = MineBot.getInstance().getNet();

        //Set public key
        networkHandler.setPublicKey(event.getPublicKey());

        //Generate & Set secret key
        SecretKey secretKey = CryptManager.createNewSharedKey();
        networkHandler.setSecretKey(secretKey);

        byte[] serverIdHash = CryptManager.getServerIdHash(event.getServerId().trim(), event.getPublicKey(), secretKey);
        if(serverIdHash == null) {
            MineBot.getLog().severe("Cannot hash server id: exiting!");
            MineBot.getInstance().setRunning(false);
            return;
        }

        String var5 = (new BigInteger(serverIdHash)).toString(16);
        String var6 = sendSessionRequest(MineBot.getInstance().getAuthData().getUsername(), "token:" + MineBot.getInstance().getAuthData().getAccessToken() + ":" + MineBot.getInstance().getAuthData().getProfile(), var5);

        networkHandler.sendPacket(new PacketOutEncryptionResponse(event.getServerId(), event.getPublicKey(), event.getVerifyToken(), secretKey));
        networkHandler.activateEncryption();
        networkHandler.decryptInputStream();
    }

    @EventHandler
    public void onLoginDisconnect(LoginDisconnectEvent event) {
        MineBot.getLog().severe("Login failed: " + event.getErrorMessage());
        MineBot.getInstance().setRunning(false);
        MineBot.getInstance().setAuthData(null);
    }

    @EventHandler
    public void onSetCompression(SetCompressionEvent event) {
        MineBot.getInstance().getNet().setThreshold(event.getThreshold());
    }

    @EventHandler
    public void onLoginSuccess(LoginSuccessEvent event) {
        MineBot.getLog().info("Login successful!");
        MineBot.getLog().info("Name: " + event.getUserName());
        MineBot.getLog().info("UUID: " + event.getUuid());
        MineBot.getInstance().getNet().setState(State.PLAY);
    }

    private String sendSessionRequest(String user, String session, String serverid) {
        try {
            return sendGetRequest("http://session.minecraft.net/game/joinserver.jsp"
                    + "?user=" + urlEncode(user)
                    + "&sessionId=" + urlEncode(session)
                    + "&serverId=" + urlEncode(serverid));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String sendGetRequest(String url) {
        try {
            URL var4 = new URL(url);
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.openStream()));
            String var6 = var5.readLine();
            var5.close();
            return var6;
        } catch (IOException var7) {
            return var7.toString();
        }
    }

    private String urlEncode(String par0Str) throws IOException {
        return URLEncoder.encode(par0Str, "UTF-8");
    }
}
