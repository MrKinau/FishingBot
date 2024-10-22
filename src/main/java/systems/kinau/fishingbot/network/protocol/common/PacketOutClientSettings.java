/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketOutClientSettings extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MC_1_8: {
                writeString("en_7s", out);  //use speach "Pirate Speak", arrr
                out.writeByte(1);           //render-distance
                out.writeByte(0);           //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
                break;
            }
            case ProtocolConstants.MC_1_13_2:
            case ProtocolConstants.MC_1_13_1:
            case ProtocolConstants.MC_1_13:
            case ProtocolConstants.MC_1_12_2:
            case ProtocolConstants.MC_1_12_1:
            case ProtocolConstants.MC_1_12:
            case ProtocolConstants.MC_1_11_1:
            case ProtocolConstants.MC_1_11:
            case ProtocolConstants.MC_1_10:
            case ProtocolConstants.MC_1_9_4:
            case ProtocolConstants.MC_1_9_2:
            case ProtocolConstants.MC_1_9_1:
            case ProtocolConstants.MC_1_9:
            case ProtocolConstants.MC_1_14:
            case ProtocolConstants.MC_1_14_1:
            case ProtocolConstants.MC_1_14_2:
            case ProtocolConstants.MC_1_14_3:
            case ProtocolConstants.MC_1_14_4:
            case ProtocolConstants.MC_1_15:
            case ProtocolConstants.MC_1_15_1:
            case ProtocolConstants.MC_1_15_2:
            case ProtocolConstants.MC_1_16:
            case ProtocolConstants.MC_1_16_1:
            case ProtocolConstants.MC_1_16_2:
            case ProtocolConstants.MC_1_16_3:
            case ProtocolConstants.MC_1_16_4:
                writeString("lol_aa", out); //use speach "LOLCAT", lol
                out.writeByte(1);           //render-distance
                writeVarInt(0, out);        //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
                writeVarInt(1, out);        //right = main hand
                break;
            case ProtocolConstants.MC_1_17:
            case ProtocolConstants.MC_1_17_1: {
                writeString("lol_aa", out); //use speach "LOLCAT", lol
                out.writeByte(1);           //render-distance
                writeVarInt(0, out);        //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
                writeVarInt(1, out);        //right = main hand
                out.writeBoolean(false);     //Disable text filtering
                break;
            }
            case ProtocolConstants.MC_1_18:
            case ProtocolConstants.MC_1_18_2:
            case ProtocolConstants.MC_1_19:
            case ProtocolConstants.MC_1_19_1:
            case ProtocolConstants.MC_1_19_3:
            case ProtocolConstants.MC_1_19_4:
            case ProtocolConstants.MC_1_20:
            case ProtocolConstants.MC_1_20_2:
            case ProtocolConstants.MC_1_20_3:
            case ProtocolConstants.MC_1_20_5:
            case ProtocolConstants.MC_1_21: {
                writeString("lol_aa", out); //use speach "LOLCAT", lol
                out.writeByte(1);           //render-distance
                writeVarInt(0, out);        //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
                writeVarInt(1, out);        //right = main hand
                out.writeBoolean(false);     //Disable text filtering
                out.writeBoolean(true);     //Allow server listings
                break;
            }
            case ProtocolConstants.MC_1_21_2:
            default: {
                writeString("lol_aa", out); //use speach "LOLCAT", lol
                out.writeByte(1);           //render-distance
                writeVarInt(0, out);        //chat enabled
                out.writeBoolean(true);     //support colors
                out.writeByte(128);         //skin bitmask
                writeVarInt(1, out);        //right = main hand
                out.writeBoolean(false);     //Disable text filtering
                out.writeBoolean(true);     //Allow server listings
                writeVarInt(2, out);     //particle status = minimal
                break;
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
