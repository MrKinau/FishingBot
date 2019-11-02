/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/15
 */

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

public class PacketInPlayerListItem extends Packet {

    @Getter private static Set<UUID> currPlayers = new HashSet<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int action = readVarInt(in);
        int playerCount = readVarInt(in);
        for(int i = 0; i < playerCount; i++) {
            UUID uuid = readUUID(in);
            if (action == 0)        //ADD
                currPlayers.add(uuid);
            else if (action == 4)   //REMOVE
                currPlayers.remove(uuid);
        }
        in.skipBytes(in.getAvailable());

        FishingBot.getInstance().getEventManager().callEvent(new UpdatePlayerListEvent(getCurrPlayers()));
    }
}
