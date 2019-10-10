package systems.kinau.fishingbot.mining;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class World {

    @Getter private int dimension;
    @Getter private String levelType;
    @Getter private List<Chunk> chunkList = new ArrayList<>();

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

}
