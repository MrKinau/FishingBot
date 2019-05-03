/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;

@AllArgsConstructor
@NoArgsConstructor
public class PacketInEncryptionRequest extends Packet {

    @Getter private String serverId;
    @Getter private PublicKey publicKey;
    @Getter private byte[] verifyToken;
    @Getter private SecretKey secretKey;

    @Override
    public void write(ByteArrayDataOutput out) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException {
        //reading public key & verify token
        this.serverId = PacketHelper.readString(in);
        this.publicKey = CryptManager.decodePublicKey(PacketHelper.readBytesFromStreamV(in));
        this.verifyToken = PacketHelper.readBytesFromStreamV(in);

        networkHandler.setPublicKey(publicKey);

        //Generate secret key
        secretKey = CryptManager.createNewSharedKey();
        networkHandler.setSecretKey(secretKey);

        String var5 = (new BigInteger(CryptManager.getServerIdHash(serverId.trim(), publicKey, secretKey))).toString(16);
        String var6 = sendSessionRequest(networkHandler.getAuthData().getUsername(), "token:" + networkHandler.getAuthData().getAccessToken() + ":" + networkHandler.getAuthData().getProfile(), var5);

        networkHandler.sendPacket(new PacketOutEncryptionResponse(serverId, publicKey, verifyToken, secretKey));
        networkHandler.activateEncryption();
        networkHandler.decryptInputStream();
    }

    private String sendSessionRequest(String user, String session, String serverid) {
        try {
            return sendGetRequest("http://session.minecraft.net/game/joinserver.jsp?user="
                    + urlEncode(user)
                    + "&sessionId="
                    + urlEncode(session)
                    + "&serverId=" + urlEncode(serverid));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String sendGetRequest(String url) {
        try {
            URL var4 = new URL(url);
            BufferedReader var5 = new BufferedReader(new InputStreamReader(
                    var4.openStream()));
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
