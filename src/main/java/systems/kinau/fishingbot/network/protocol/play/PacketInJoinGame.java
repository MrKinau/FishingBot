/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.JoinGameEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInJoinGame extends Packet {

    @Getter private int eid;
    @Getter private int gamemode;
    @Getter private int dimension;
    @Getter private int difficulty;
    @Getter private int maxPlayers;
    @Getter private int viewDistance;
    @Getter private String levelType;
    @Getter private boolean reducedDebugInfo;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_8: {
                eid = in.readInt();                      //Entity ID
                gamemode = in.readUnsignedByte();        //Gamemode
                dimension = in.readByte();               //Dimension
                difficulty = in.readUnsignedByte();      //Difficulty
                maxPlayers = in.readUnsignedByte();      //MaxPlayer
                levelType = readString(in);              //level type
                reducedDebugInfo = in.readBoolean();     //Reduced Debug info
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                eid = in.readInt();                     //Entity ID
                gamemode = in.readUnsignedByte();       //Gamemode
                dimension = in.readInt();               //Dimension
                maxPlayers = in.readUnsignedByte();     //MaxPlayer
                levelType = readString(in);             //level type
                viewDistance = readVarInt(in);          //View distance
                reducedDebugInfo = in.readBoolean();    //Reduced Debug info
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1: {
                eid = in.readInt();                     //Entity ID
                gamemode = in.readUnsignedByte();       //Gamemode
                dimension = in.readInt();               //Dimension
                difficulty = in.readUnsignedByte();     //Difficulty
                maxPlayers = in.readUnsignedByte();     //MaxPlayer
                levelType = readString(in);             //level type
                reducedDebugInfo = in.readBoolean();    //Reduced Debug info
                break;
            }
        }

        FishingBot.getInstance().getEventManager().callEvent(new JoinGameEvent(eid, gamemode, dimension, difficulty, maxPlayers, viewDistance, levelType, reducedDebugInfo));
    }
}
