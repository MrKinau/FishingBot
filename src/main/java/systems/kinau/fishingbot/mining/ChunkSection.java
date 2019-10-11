/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/10
 */

package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ChunkSection {

    @Getter private int yStart;

    private short[][][] blocks = new short[16][16][16];

    public ChunkSection(ByteArrayDataInputWrapper in, int chunkY) {
        this.yStart = chunkY * 16;
        for(int currY = 0; currY < 16; currY++) {
            for(int currZ = 0; currZ < 16; currZ++) {
                for(int currX = 0; currX < 16; currX++) {
                    int blockByte1 = in.readUnsignedByte();
                    int blockByte2 = in.readUnsignedByte();
                    short block = (short) ((blockByte2 << 8) | blockByte1);
                    blocks[currX][currY][currZ] = block;
                }
            }
        }
    }

    public BlockType getBlockAt(int relativeX, int relativeY, int relativeZ) {
        short block = blocks[relativeX][relativeY][relativeZ];
        int id = (block & 0xfff0) >> 4;
        int data = block & 0xF;
        return new BlockType(id, data);
    }
}
