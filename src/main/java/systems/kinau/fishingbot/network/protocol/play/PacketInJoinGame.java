/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInJoinGame extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_8: {
                in.readInt();       //Entity ID
                in.readByte();      //Gamemode
                in.readByte();      //Dimension
                in.readByte();      //Difficulty
                in.readByte();      //MaxPlayer
                readString(in);     //level type
                in.readBoolean();   //Reduced Debug info
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1: {
                in.readInt();       //Entity ID
                in.readByte();      //Gamemode
                in.readByte();      //Dimension
                in.readByte();      //MaxPlayer
                readString(in);     //level type
                readVarInt(in);     //View distance
                in.readBoolean();   //Reduced Debug info
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
                in.readInt();       //Entity ID
                in.readByte();      //Gamemode
                in.readInt();       //Dimension
                in.readByte();      //MaxPlayer
                in.readByte();      //Difficulty
                readString(in);     //level type
                in.readBoolean();   //Reduced Debug info
                break;
            }
        }

        networkHandler.sendPacket(new PacketOutClientSettings());
    }
}
