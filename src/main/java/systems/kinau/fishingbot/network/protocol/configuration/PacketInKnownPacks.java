package systems.kinau.fishingbot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.configuration.KnownPacksRequestedEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class PacketInKnownPacks extends Packet {

    @Getter private final List<KnownPack> knownPacks = new LinkedList<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        for (int i = 0; i < count; i++) {
            String name = readString(in);
            String id = readString(in);
            String version = readString(in);
            knownPacks.add(new KnownPack(name, id, version));
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new KnownPacksRequestedEvent(knownPacks));
    }

    @Data
    public static class KnownPack {
        private final String namespace;
        private final String id;
        private final String version;
    }
}
