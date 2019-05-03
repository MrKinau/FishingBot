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
    public void write(ByteArrayDataOutput out) throws IOException {
        byte[] sharedSecret = new byte[0];
        byte[] verifyToken = new byte[0];
        sharedSecret = CryptManager.encryptData(getPublicKey(), getSecretKey().getEncoded());
        verifyToken = CryptManager.encryptData(getPublicKey(), getVerifyToken());
        PacketHelper.writeVarInt(out, sharedSecret.length);
        out.write(sharedSecret);
        PacketHelper.writeVarInt(out, verifyToken.length);
        out.write(verifyToken);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException { }
}
