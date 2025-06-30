package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

/**
 * Created: 05.11.2020
 *
 * @author Summerfeeling
 */
// This is the Player Command Packet according to the wiki.vg name
@AllArgsConstructor
@Getter
public class PacketOutEntityAction extends Packet {

    private final EntityAction entityAction;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if (entityAction == EntityAction.OTHER) {
            throw new IllegalArgumentException("EntityAction#OTHER is not allowed in PacketOutEntityAction.");
        }

        Packet.writeVarInt(FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID(), out);   // Entity ID
        Packet.writeVarInt(entityAction.getId(protocolId), out);                               // Action ID (only supported 0-4, see EntityAction enum)
        Packet.writeVarInt(0, out);                                              // Horse jump strength
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }

    public enum EntityAction {
        START_SNEAKING,
        STOP_SNEAKING,
        LEAVE_BED,
        START_SPRINTING,
        STOP_SPRINTING,
        OTHER,
        ;

        public int getId(int protocolId) {
            if (protocolId < ProtocolConstants.MC_1_21_6)
                return ordinal();
            return ordinal() - 2; // start and stop sneaking removed
        }
    }

}
