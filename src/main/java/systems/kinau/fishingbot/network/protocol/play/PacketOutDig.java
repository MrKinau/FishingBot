/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutDig extends Packet {

    @Getter private byte digStatus;
    @Getter private int x;
    @Getter private int y;
    @Getter private int z;
    @Getter private byte face;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeByte(digStatus);
        out.writeLong(((long)(x & 0x3FFFFFF) << 38) | ((long)(y & 0xFFF) << 26) | z & 0x3FFFFFF);
        out.writeByte(face);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException { }

}
