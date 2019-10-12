/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/10
 */

package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class ChunkSection {

    @Getter private int yStart;

    private short[][][] blocks = new short[16][16][16];

    public static ChunkSection newEmpty(int yStart) {
        short[][][] airBlocks = new short[16][16][16];
        for (int currY = 0; currY < 16; currY++) {
            for (int currZ = 0; currZ < 16; currZ++) {
                for (int currX = 0; currX < 16; currX++) {
                    airBlocks[currX][currY][currZ] = 0;
                }
            }
        }
        return new ChunkSection(airBlocks, yStart);
    }

    public ChunkSection(ByteArrayDataInputWrapper in, int chunkY) {
        this.yStart = chunkY * 16;
        for(int currY = 0; currY < 16; currY++) {
            for(int currZ = 0; currZ < 16; currZ++) {
                for(int currX = 0; currX < 16; currX++) {
                    short block = 0;
                    try {
                        block = Packet.readBlock(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    blocks[currX][currY][currZ] = block;
                }
            }
        }
    }

    private ChunkSection(short[][][] blocks, int yStart) {
        this.yStart = yStart;
        this.blocks = blocks;
    }

    public BlockType getBlockAt(int relativeX, int relativeY, int relativeZ) {
        return new BlockType(blocks[relativeX][relativeY][relativeZ]);
    }

    public void setBlockAt(int relativeX, int relativeY, int relativeZ, short block) {
        blocks[relativeX][relativeY][relativeZ] = block;
    }
}
