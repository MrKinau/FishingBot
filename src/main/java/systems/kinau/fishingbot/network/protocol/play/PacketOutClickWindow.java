package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.Map;

@Getter
@AllArgsConstructor
public class PacketOutClickWindow extends Packet {

    private int windowId;
    private short slot;
    private byte button;
    private short actionNumber;
    private int mode;
    private Slot item;
    private Map<Short, Slot> remaining;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() <= ProtocolConstants.MC_1_16_4) {
            writeContainerId(windowId, out, protocolId);
            out.writeShort(slot);
            out.writeByte(button);
            out.writeShort(actionNumber);
            writeVarInt(mode, out);
            writeSlot(item, out, protocolId);
        } else {
            writeContainerId(windowId, out, protocolId);
            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_17_1)
                writeVarInt(0, out); // revision
            out.writeShort(slot);
            out.writeByte(button);
            writeVarInt(mode, out);
            writeVarInt(remaining.size(), out);
            for (Map.Entry<Short, Slot> remainingSlot : remaining.entrySet()) {
                out.writeShort(remainingSlot.getKey());
                writeSlot(remainingSlot.getValue(), out, protocolId);
            }
            writeSlot(item, out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
