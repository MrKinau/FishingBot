package systems.kinau.fishingbot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.function.BiConsumer;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutPluginMessage extends Packet {

    private String channel;
    private BiConsumer<ByteArrayDataOutput, Integer> data;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        writeString(channel, out);
        data.accept(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
