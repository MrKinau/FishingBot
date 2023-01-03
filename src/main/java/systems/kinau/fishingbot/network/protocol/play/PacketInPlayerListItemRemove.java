package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.UpdatePlayerListEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketInPlayerListItemRemove extends Packet {

    @Getter
    private Set<UUID> players = new HashSet<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int count = readVarInt(in);
        for (int i = 0; i < count; i++) {
            players.add(readUUID(in));
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdatePlayerListEvent(UpdatePlayerListEvent.Action.REMOVE, players));
    }
}
