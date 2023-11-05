package systems.kinau.fishingbot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.configuration.ConfigurationFinishEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
public class PacketInFinishConfiguration extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ConfigurationFinishEvent());
    }
}
