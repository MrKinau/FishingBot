/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import java.io.IOException;

public class PacketOutClientSettings extends Packet {

    @Override
    public void write(ByteArrayDataOutput out) throws IOException {
        PacketHelper.writeString(out, "lol_aa");
        out.writeByte(1); //render-distance
        PacketHelper.writeVarInt(out, 2); //chat hidden
        out.writeBoolean(true); //support colors
        out.writeByte(128); //skin bitmask
        PacketHelper.writeVarInt(out, 1); //right = main hand
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException { }
}
