/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;

import java.util.BitSet;
import java.util.Optional;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PacketOutChatMessage extends Packet {

    private String message;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(getMessage(), out);
        if (protocolId >= ProtocolConstants.MC_1_19 && protocolId <= ProtocolConstants.MC_1_19_1) {
            AuthData.ProfileKeys keys = FishingBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            UUID signer = null;
            try {
                signer = UUID.fromString(FishingBot.getInstance().getCurrentBot().getAuthData().getUuid());
            } catch (Exception ignore) {}

            if (keys == null || signer == null) {
                out.writeLong(System.currentTimeMillis());  // timestamp
                // this is most likely very illegal, but it seems like the server does not care about the signature
                out.writeLong(System.currentTimeMillis());  // sig pair long
                writeVarInt(1, out);                  // sig pair bytearray
                out.write(new byte[]{1});                   // sig pair bytearray
                out.writeBoolean(false);                 // signed preview
            } else {
                CryptManager.MessageSignature signature = CryptManager.signChatMessage(keys, signer, message);
                out.writeLong(signature.getTimestamp().toEpochMilli());
                out.writeLong(signature.getSalt());
                writeVarInt(signature.getSignature().length, out);
                out.write(signature.getSignature());
                out.writeBoolean(false);

                FishingBot.getInstance().getCurrentBot().getPlayer().setLastUsedSignature(Optional.of(signature));
            }

            if (protocolId >= ProtocolConstants.MC_1_19_1) {
                writeVarInt(0, out);
                out.writeBoolean(false);
            }
        } else if (protocolId >= ProtocolConstants.MC_1_19_3) {
            AuthData.ProfileKeys keys = FishingBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            UUID signer = null;
            try {
                signer = UUID.fromString(FishingBot.getInstance().getCurrentBot().getAuthData().getUuid());
            } catch (Exception ignore) {}


            if (keys == null || signer == null) {
                out.writeLong(System.currentTimeMillis());  // timestamp
                // this is most likely very illegal, but it seems like the server does not care about the signature
                out.writeLong(System.currentTimeMillis());  // sig pair long
                out.writeBoolean(false);                 // no sig present
                writeVarInt(0, out);                  // lastSeen sigs offset?
                writeFixedBitSet(new BitSet(), 20, out);
                if (protocolId >= ProtocolConstants.MC_1_21_5)
                    out.writeByte(0);                        // checksum, always 0 should be ok
            } else {
                CryptManager.MessageSignature signature = CryptManager.signChatMessage(keys, signer, message);
                out.writeLong(signature.getTimestamp().toEpochMilli());
                out.writeLong(signature.getSalt());
                out.writeBoolean(true);
                out.write(signature.getSignature());
                writeVarInt(0, out);                  // lastSeen sigs offset?
                writeFixedBitSet(new BitSet(), 20, out);
                if (protocolId >= ProtocolConstants.MC_1_21_5)
                    out.writeByte(0);                        // checksum, always 0 should be ok
                FishingBot.getInstance().getCurrentBot().getPlayer().setLastUsedSignature(Optional.of(signature));
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
