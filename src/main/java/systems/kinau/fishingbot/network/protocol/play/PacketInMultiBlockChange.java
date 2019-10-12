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

public class PacketInMultiBlockChange extends Packet {

    @Getter private int chunkX;
    @Getter private int chunkZ;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        chunkX = in.readInt();
        chunkZ = in.readInt();
        int recordCount = readVarInt(in);

        for(int i = 0; i < recordCount; i++) {
            int horizPos = in.readUnsignedByte();
            int vertPos = in.readUnsignedByte();;
            short block = (short) readVarInt(in);
            int x = (horizPos >> 4 & 15) + (chunkX * 16);
            int y = vertPos;
            int z = (horizPos & 15) + (chunkZ * 16);

            MineBot.getInstance().getEventManager().callEvent(new BlockChangeEvent(x, y, z, new BlockType(block)));
        }
    }
}
