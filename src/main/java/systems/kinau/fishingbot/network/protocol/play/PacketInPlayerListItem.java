/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/15
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.PingChangeEvent;
import systems.kinau.fishingbot.event.play.UpdatePlayerListEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.EnumSet;
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
        if (protocolId <= ProtocolConstants.MINECRAFT_1_19_1) {
            int action = readVarInt(in);
            int playerCount = readVarInt(in);
            for (int i = 0; i < playerCount; i++) {
                UUID uuid = readUUID(in);
                if (action == 0) {        //ADD
                    readString(in); // name
                    currPlayers.add(uuid);
                    int propCount = readVarInt(in);
                    for (int j = 0; j < propCount; j++) {
                        readString(in); // name
                        readString(in); // value
                        if (in.readBoolean()) {// signed
                            readString(in); // signature
                        }
                    }
                    readVarInt(in); // gamemode
                    int ping = readVarInt(in);
                    if (in.readBoolean()) // has display name
                        readString(in); // display name
                    if (protocolId >= ProtocolConstants.MINECRAFT_1_19) {
                        if (in.readBoolean()) {
                            in.readLong(); // expiration
                            int size = readVarInt(in); // pubkey length
                            in.readFully(new byte[size]); // pubkey
                            size = readVarInt(in); // sig length
                            in.readFully(new byte[size]); // sig
                        }
                    }
                    if (uuid.equals(FishingBot.getInstance().getCurrentBot().getPlayer().getUuid()))
                        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new PingChangeEvent(ping));
                } else if (action == 1) {
                    readVarInt(in); // gamemode
                } else if (action == 2) {
                    int ping = readVarInt(in);
                    if (uuid.equals(FishingBot.getInstance().getCurrentBot().getPlayer().getUuid()))
                        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new PingChangeEvent(ping));
                } else if (action == 3) {
                    if (in.readBoolean()) // has display name
                        readString(in); // display name
                } else if (action == 4) {  //REMOVE
                    currPlayers.remove(uuid);
                }
            }
            in.skipBytes(in.getAvailable());
            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdatePlayerListEvent(UpdatePlayerListEvent.Action.REPLACE, getCurrPlayers()));
        } else {
            Set<UUID> addedPlayers = new HashSet<>();
            EnumSet<Action> actions = readEnumSet(Action.class, in);
            int count = readVarInt(in);
            for (int i = 0; i < count; i++) {
                UUID uuid = readUUID(in);
                for(Action action : actions) {
                    if (action == Action.ADD_PLAYER)
                        addedPlayers.add(uuid);
                    action.reader.read(in);
                }
            }
            in.skipBytes(in.getAvailable());
            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdatePlayerListEvent(UpdatePlayerListEvent.Action.ADD, addedPlayers));
        }
    }

    enum Action {
        ADD_PLAYER((in) -> {
            readString(in);
            int propCount = readVarInt(in);
            for (int j = 0; j < propCount; j++) {
                readString(in); // name
                readString(in); // value
                if (in.readBoolean()) {// signed
                    readString(in); // signature
                }
            }
        }),
        INITIALIZE_CHAT((in) -> {
            if (in.readBoolean()) {
                readUUID(in);
                in.readLong(); // expiresAt
                in.skipBytes(512); // public key
                in.skipBytes(4096); // public key signature
            }
        }),
        UPDATE_GAME_MODE(Packet::readVarInt),
        UPDATE_LISTED(ByteArrayDataInputWrapper::readBoolean),
        UPDATE_LATENCY(Packet::readVarInt),
        UPDATE_DISPLAY_NAME(Packet::readString);

        final Action.Reader reader;

        private Action(Action.Reader reader) {
            this.reader = reader;
        }

        public interface Reader {
            void read(ByteArrayDataInputWrapper input);
        }
    }
}
