/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketOutClientSettings extends Packet {

    @Override
    public void write(ByteArrayDataOutput out) {
        writeString("lol_aa", out); //use speach "LOLCAT", lol
        out.writeByte(1); //render-distance
        writeVarInt(0, out); //chat enabled
        out.writeBoolean(true); //support colors
        out.writeByte(128); //skin bitmask
        writeVarInt(1, out); //right = main hand
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) { }
}
