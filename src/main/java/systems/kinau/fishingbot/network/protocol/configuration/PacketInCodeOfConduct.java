package systems.kinau.fishingbot.network.protocol.configuration;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.configuration.CodeOfConductEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class PacketInCodeOfConduct extends Packet {

    private String codeOfConduct;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.codeOfConduct = readString(in);
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new CodeOfConductEvent(codeOfConduct));
    }
}
