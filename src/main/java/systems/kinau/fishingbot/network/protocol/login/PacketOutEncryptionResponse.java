/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;

@NoArgsConstructor
@AllArgsConstructor
public class PacketOutEncryptionResponse extends Packet {

    @Getter private String serverId;
    @Getter private PublicKey publicKey;
    @Getter private byte[] verifyToken;
    @Getter private SecretKey secretKey;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        byte[] sharedSecret = CryptManager.encryptData(getPublicKey(), getSecretKey().getEncoded());
        byte[] verifyToken = CryptManager.encryptData(getPublicKey(), getVerifyToken());
        writeVarInt(sharedSecret.length, out);
        out.write(sharedSecret);
        writeVarInt(verifyToken.length, out);
        out.write(verifyToken);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException { }
}
