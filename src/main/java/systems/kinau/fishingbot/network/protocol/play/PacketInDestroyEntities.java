package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.DestroyEntitiesEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class PacketInDestroyEntities extends Packet {

    @Getter private List<Integer> entityIds;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        this.entityIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            getEntityIds().add(readVarInt(in));
        }

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new DestroyEntitiesEvent(getEntityIds()));
    }
}
