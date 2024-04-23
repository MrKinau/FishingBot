package systems.kinau.fishingbot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class PacketOutKnownPacks extends Packet {

    @Getter private List<PacketInKnownPacks.KnownPack> knownPacks = new LinkedList<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        writeVarInt(knownPacks.size(), out);
        for (PacketInKnownPacks.KnownPack pack : knownPacks) {
            writeString(pack.getNamespace(), out);
            writeString(pack.getId(), out);
            writeString(pack.getVersion(), out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
