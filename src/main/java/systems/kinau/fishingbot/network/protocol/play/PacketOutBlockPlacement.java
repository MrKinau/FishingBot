/*
 * Created by David Luedtke (MrKinau)
 * 2019/11/2
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.Arrays;

@AllArgsConstructor
public class PacketOutBlockPlacement extends Packet {

    @Getter private int x;
    @Getter private int y;
    @Getter private int z;
    @Getter private byte face;
    @Getter private byte[] slot;
    @Getter private byte cursorX;
    @Getter private byte cursorY;
    @Getter private byte cursorZ;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeLong(((long)(x & 0x3FFFFFF) << 38) | ((long)(y & 0xFFF) << 26) | z & 0x3FFFFFF);
        out.writeByte(face); //unsined, lol
        out.write(slot);
        System.out.println(Arrays.toString(slot));
        out.writeByte(cursorX); //unsined, lol
        out.writeByte(cursorY); //unsined, lol
        out.writeByte(cursorZ); //unsined, lol
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException { }

    public static PacketOutBlockPlacement useItem() {
        return new PacketOutBlockPlacement(-1, -1, -1, (byte)255, MineBot.getInstance().getPlayer().getSlotData().toByteArray(), (byte)0, (byte)0, (byte)0);
    }
}
