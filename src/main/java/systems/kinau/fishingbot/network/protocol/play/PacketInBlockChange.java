/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/12
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.block.BlockChangeEvent;
import systems.kinau.fishingbot.mining.BlockType;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class PacketInBlockChange extends Packet {

    @Getter private int x;
    @Getter private int y;
    @Getter private int z;
    @Getter private int block;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        long val = in.readLong();
        x = Double.valueOf(Math.floor(val >> 38)).intValue();
        y = Double.valueOf(Math.floor(val << 26 >> 52)).intValue();
        z = Double.valueOf(Math.floor(val << 38 >> 38)).intValue();
        block = readVarInt(in);

        MineBot.getInstance().getEventManager().callEvent(new BlockChangeEvent(x, y, z, new BlockType(block)));
    }
}
