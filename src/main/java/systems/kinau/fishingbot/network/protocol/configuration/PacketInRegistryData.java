package systems.kinau.fishingbot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.configuration.RegistryDataEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

@Getter
@NoArgsConstructor
public class PacketInRegistryData extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        String registryId = readString(in);
        int count = readVarInt(in);
        SortedMap<String, @Nullable NBTTag> data = new TreeMap<>();
        for (int i = 0; i < count; i++) {
            String identifier = readString(in);
            if (in.readBoolean()) {
                data.put(identifier, readNBT(in, protocolId));
            } else {
                data.put(identifier, null);
            }
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new RegistryDataEvent(registryId, data));
    }
}
