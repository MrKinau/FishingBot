/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Random;

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
        if (protocolId < ProtocolConstants.MINECRAFT_1_19) {
            writeVarInt(verifyToken.length, out);
            out.write(verifyToken);
        } else {
            AuthData.ProfileKeys keys = FishingBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            out.writeBoolean(keys == null);
            if (keys == null) {
                writeVarInt(verifyToken.length, out);
                out.write(verifyToken);
            } else {
                long salt = new Random().nextLong();
                byte[] signed = CryptManager.sign(keys, signature -> {
                    try {
                        signature.update(getVerifyToken());
                        signature.update(Longs.toByteArray(salt));
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    }
                });
                out.writeLong(salt);
                writeVarInt(signed.length, out);
                out.write(signed);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException { }
}
