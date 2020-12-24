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

public class PacketOutClientSettings extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                writeString("en_7s", out);  //use speach "Pirate Speak", arrr
                out.writeByte(1);           //render-distance
                out.writeByte(0);           //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
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
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                writeString("lol_aa", out); //use speach "LOLCAT", lol
                out.writeByte(1);           //render-distance
                writeVarInt(0, out);        //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
                writeVarInt(1, out);        //right = main hand
                break;
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
