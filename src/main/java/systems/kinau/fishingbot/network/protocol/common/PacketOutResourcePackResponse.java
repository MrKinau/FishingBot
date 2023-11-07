package systems.kinau.fishingbot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutResourcePackResponse extends Packet {

    @Getter private Result result;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        writeVarInt(result.ordinal(), out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }

    public enum Result {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED
    }
}
