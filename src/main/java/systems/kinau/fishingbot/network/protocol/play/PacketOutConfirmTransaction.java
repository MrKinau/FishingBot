package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutConfirmTransaction extends Packet {

    @Getter private byte windowId;
    @Getter private short action;
    @Getter private boolean accepted;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeByte(windowId);
        out.writeShort(action);
        out.writeBoolean(accepted);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
