package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class World {

    @Getter
    private int dimension;
    @Getter
    private String levelType;
    @Getter
    private List<Chunk> chunkList = new CopyOnWriteArrayList<>();

    public World(int dimension, String levelType) {
        this.dimension = dimension;
        this.levelType = levelType;
    }

    public void addChunk(Chunk chunk) {
        chunkList.add(chunk);
    }

    public Optional<Chunk> getChunk(int chunkX, int chunkZ) {
        return chunkList.stream()
                .filter(chunk -> chunk.getChunkX() == chunkX)
                .filter(chunk -> chunk.getChunkZ() == chunkZ)
                .findAny();
    }

    public BlockType getBlockAt(int x, int y, int z) {
        Optional<Chunk> optChunk = chunkList.stream()
                .filter(chunk -> x >= (chunk.getChunkX() * 16))
                .filter(chunk -> x < (chunk.getChunkX() * 16 + 16))
                .filter(chunk -> z >= (chunk.getChunkZ() * 16))
                .filter(chunk -> z < (chunk.getChunkZ() * 16 + 16))
                .findAny();

        if (!optChunk.isPresent()) {
            MineBot.getLog().severe("Tried to get block of unloaded chunk at " + x + "/" + y + "/" + z);
            return BlockType.AIR;
        }

        Chunk c = optChunk.get();
        return c.getBlockAt(x - (c.getChunkX() * 16), y, z - (c.getChunkZ() * 16));
    }

    public void unloadChunk(Chunk chunk) {
        if(chunkList.contains(chunk))
            MineBot.getLog().info("Removed Chunk: " + chunk.getChunkX() + "/" + chunk.getChunkZ());
        else
            MineBot.getLog().info("Could not unload chunk: " + chunk.getChunkX() + "/" + chunk.getChunkZ());
        chunkList.remove(chunk);
    }

}
