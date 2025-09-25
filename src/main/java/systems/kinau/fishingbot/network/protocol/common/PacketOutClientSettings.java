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
        if (protocolId <= ProtocolConstants.MC_1_8) {
            writeString("en_7s", out);  //use speach "Pirate Speak", arrr
            out.writeByte(2);           //render-distance
            out.writeByte(0);           //chat enabled
            out.writeBoolean(true);     //support colors
            out.writeByte(128);         //skin bitmask
        } else if (protocolId <= ProtocolConstants.MC_1_13_2) {
            writeString("lol_aa", out); //use speach "LOLCAT", lol
            out.writeByte(2);           //render-distance
            writeVarInt(0, out);     //chat enabled
            out.writeBoolean(true);     //support colors
            out.writeByte(128);         //skin bitmask
            writeVarInt(1, out);     //right = main hand
        } else if (protocolId <= ProtocolConstants.MC_1_17_1) {
            writeString("lol_aa", out); //use speach "LOLCAT", lol
            out.writeByte(2);           //render-distance
            writeVarInt(0, out);     //chat enabled
            out.writeBoolean(true);     //support colors
            out.writeByte(128);         //skin bitmask
            writeVarInt(1, out);     //right = main hand
            out.writeBoolean(false);    //Disable text filtering
        } else if (protocolId <= ProtocolConstants.MC_1_21) {
            writeString("lol_aa", out); //use speach "LOLCAT", lol
            out.writeByte(2);           //render-distance
            writeVarInt(0, out);     //chat enabled
            out.writeBoolean(true);     //support colors
            out.writeByte(128);         //skin bitmask
            writeVarInt(1, out);     //right = main hand
            out.writeBoolean(false);    //Disable text filtering
            out.writeBoolean(true);     //Allow server listings
        } else {
            writeString("lol_aa", out); //use speach "LOLCAT", lol
            out.writeByte(2);           //render-distance
            writeVarInt(0, out);     //chat enabled
            out.writeBoolean(true);     //support colors
            out.writeByte(128);         //skin bitmask
            writeVarInt(1, out);     //right = main hand
            out.writeBoolean(false);    //Disable text filtering
            out.writeBoolean(true);     //Allow server listings
            writeVarInt(2, out);     //particle status = minimal
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
