/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutLoginStart extends Packet {

    private String userName;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(userName, out);
        if (protocolId >= ProtocolConstants.MINECRAFT_1_19) {
            AuthData.ProfileKeys keys = FishingBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            out.writeBoolean(keys != null);
            if (keys != null) {
                out.writeLong(keys.getExpiresAt());
                byte[] pubKey = keys.getPublicKey().getEncoded();
                writeVarInt(pubKey.length, out);
                out.write(pubKey);
                byte[] signature = ByteBuffer.wrap(Base64.getDecoder().decode(keys.getPublicKeySignature())).array();
                writeVarInt(signature.length, out);
                out.write(signature);
            }
            if (protocolId >= ProtocolConstants.MINECRAFT_1_19_1) {
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(FishingBot.getInstance().getCurrentBot().getAuthData().getUuid());
                } catch (Exception ignore) {
                }
                out.writeBoolean(uuid != null);
                if (uuid != null) {
                    out.writeLong(uuid.getMostSignificantBits());
                    out.writeLong(uuid.getLeastSignificantBits());
                }
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}
