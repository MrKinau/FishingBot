package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutClickWindow extends Packet {

    @Getter private int windowId;
    @Getter private short slot;
    @Getter private byte button;
    @Getter private short actionNumber;
    @Getter private int mode;
    @Getter private Slot item;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeByte(getWindowId());
        out.writeShort(slot);
        out.writeByte(button);
        out.writeShort(actionNumber);
        writeVarInt(mode, out);
        writeSlot(item, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
