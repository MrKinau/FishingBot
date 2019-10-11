/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/10
 */

package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Chunk {

    @Getter private List<ChunkSection> sections = new ArrayList<>();

    @Getter private  int bitmask;
    @Getter private int chunkX;
    @Getter private int chunkZ;
    @Getter private int id;

    public Chunk(int chunkX, int chunkZ, int bitmask) {
        this.id = MineBot.getInstance().getWorld().getChunkList().size();
        this.bitmask = bitmask;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void addSection(ChunkSection section) {
        sections.add(section);
    }

    public BlockType getBlockAt(int relativeX, int relativeY, int relativeZ) {
        Optional<ChunkSection> optChunkSection = sections.stream()
                .filter(section -> section.getYStart() <= relativeY)
                .filter(section -> relativeY < (section.getYStart() + 16))
                .findAny();
        if (optChunkSection.isPresent())
            return optChunkSection.get().getBlockAt(relativeX, relativeY - optChunkSection.get().getYStart(), relativeZ);
        else {
            MineBot.getLog().info("Could not find section at: " + relativeY);
            return BlockType.AIR;
        }
    }

    public void loadSections(ByteArrayDataInputWrapper in, boolean skipLightning, boolean skipBiome) {
        for (int sectionY = 0; sectionY < 16; sectionY++) {
            if(in.getAvailable() <= 256) {  //chunk unload
                Optional<Chunk> chunk = MineBot.getInstance().getWorld().getChunk(chunkX, chunkZ);
                chunk.ifPresent(value -> MineBot.getInstance().getWorld().unloadChunk(value));
                in.skipBytes(in.getAvailable());
                return;
            }
            if (((bitmask & (1 << sectionY)) != 0)) {
                ChunkSection chunkSection = new ChunkSection(in, sectionY);
                addSection(chunkSection);
//                MineBot.getLog().info("Loaded chunk section of " + (chunkX * 16) + "/" + (chunkZ * 16) + " from y:" + (sectionY * 16) + " to y:" + (sectionY * 16 + 15));
            }
        }
//        MineBot.getLog().info("Fully loaded Chunk (#" + getId() + ") at: " + (chunkX * 16) + "/" + (chunkZ * 16) + " with " + getSections().size() + " ChunkSections");

        if(skipLightning)
            in.skipBytes(getSections().size() * 4096); //skip lightning
        if(skipBiome)
            in.skipBytes(256); //skip biome
//        MineBot.getLog().info("left: " + in.getAvailable() + " bytes!");
    }


}
