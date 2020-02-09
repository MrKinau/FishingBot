package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.block.BlockChangeEvent;
import systems.kinau.fishingbot.network.utils.MaterialMc18;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class World implements Listener {

    @Getter private int dimension;
    @Getter private String levelType;
    @Getter private List<Chunk> chunkList = new CopyOnWriteArrayList<>();

    public World(int dimension, String levelType) {
        this.dimension = dimension;
        this.levelType = levelType;
        MineBot.getInstance().getEventManager().registerListener(this);
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

    public Position getRelativePosition(int x, int y, int z, byte blockFace) {
        switch (blockFace) {
            case BlockFace.X_NEGATIVE: x--; break;
            case BlockFace.X_POSITIVE: x++; break;
            case BlockFace.Z_NEGATIVE: z--; break;
            case BlockFace.Z_POSITIVE: z++; break;
            case BlockFace.UP: y++; break;
            case BlockFace.DOWN: y--; break;
        }
        return new Position(x, y, z);
    }

    public BlockType getBlockAt(Position pos) {
        return getBlockAt(pos.getX(), pos.getY(), pos.getZ(), BlockFace.NONE);
    }

    public BlockType getBlockAt(int x, int y, int z) {
        return getBlockAt(x, y, z, BlockFace.NONE);
    }

    public BlockType getBlockAt(int x, int y, int z, byte blockFace) {
        Position pos = getRelativePosition(x, y, z, blockFace);

        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();

        int finalX = x;
        int finalZ = z;
        Optional<Chunk> optChunk = chunkList.stream()
                .filter(chunk -> finalX >= (chunk.getChunkX() * 16))
                .filter(chunk -> finalX < (chunk.getChunkX() * 16 + 16))
                .filter(chunk -> finalZ >= (chunk.getChunkZ() * 16))
                .filter(chunk -> finalZ < (chunk.getChunkZ() * 16 + 16))
                .findAny();

        if (!optChunk.isPresent()) {
            MineBot.getLog().severe("Tried to get block of unloaded chunk at " + x + "/" + y + "/" + z);
            return BlockType.AIR;
        }

        Chunk c = optChunk.get();
        return c.getBlockAt(x - (c.getChunkX() * 16), y, z - (c.getChunkZ() * 16));
    }

    public byte getAdjacentDirection(Position from, Position to) {
        if (from.getY() > to.getY()) {
            return BlockFace.UP;
        } else if (from.getY() < to.getY()) {
            return BlockFace.DOWN;
        } else if (from.getX() > to.getX()) {
            return BlockFace.X_POSITIVE;
        } else if (from.getX() < to.getX()) {
            return BlockFace.X_NEGATIVE;
        } else if (from.getZ() > to.getZ()) {
            return BlockFace.Z_NEGATIVE;
        } else if (from.getZ() < to.getZ()) {
            return BlockFace.Z_POSITIVE;
        } else {
            return BlockFace.NONE;
        }
    }

    public Position getAdjacentBlock(Position pos) {
        BlockType block;
        if (!(block = getBlockAt(pos.getX(), pos.getY() + 1, pos.getZ())).getMaterial().isFluid() && block.getMaterial() != MaterialMc18.AIR) {
            return new Position(pos.getX(), pos.getY() + 1, pos.getZ());
        } else if (!(block = getBlockAt(pos.getX(), pos.getY() - 1, pos.getZ())).getMaterial().isFluid() && block.getMaterial() != MaterialMc18.AIR) {
            return new Position(pos.getX(), pos.getY() - 1, pos.getZ());
        } else if (!(block = getBlockAt(pos.getX() + 1, pos.getY(), pos.getZ())).getMaterial().isFluid() && block.getMaterial() != MaterialMc18.AIR) {
            return new Position(pos.getX() + 1, pos.getY(), pos.getZ());
        } else if (!(block = getBlockAt(pos.getX() - 1, pos.getY(), pos.getZ())).getMaterial().isFluid() && block.getMaterial() != MaterialMc18.AIR) {
            return new Position(pos.getX() - 1, pos.getY(), pos.getZ());
        } else if (!(block = getBlockAt(pos.getX(), pos.getY(), pos.getZ() + 1)).getMaterial().isFluid() && block.getMaterial() != MaterialMc18.AIR) {
            return new Position(pos.getX(), pos.getY(), pos.getZ() + 1);
        } else if (!(block = getBlockAt(pos.getX(), pos.getY(), pos.getZ() - 1)).getMaterial().isFluid() && block.getMaterial() != MaterialMc18.AIR) {
            return new Position(pos.getX(), pos.getY(), pos.getZ() - 1);
        } else {
            return null;
        }
    }

    public Set<Position> getNeighbouringBlocks(Position pos) {
        Set<Position> blocks = new HashSet<>();
        blocks.add(getRelativePosition(pos.getX(), pos.getY(), pos.getZ(), BlockFace.UP));
        blocks.add(getRelativePosition(pos.getX(), pos.getY(), pos.getZ(), BlockFace.DOWN));
        blocks.add(getRelativePosition(pos.getX(), pos.getY(), pos.getZ(), BlockFace.X_NEGATIVE));
        blocks.add(getRelativePosition(pos.getX(), pos.getY(), pos.getZ(), BlockFace.X_POSITIVE));
        blocks.add(getRelativePosition(pos.getX(), pos.getY(), pos.getZ(), BlockFace.Z_NEGATIVE));
        blocks.add(getRelativePosition(pos.getX(), pos.getY(), pos.getZ(), BlockFace.Z_POSITIVE));
        return blocks;
    }

    public void setBlockAt(int x, int y, int z, short block) {
        Optional<Chunk> optChunk = getChunkAt(x, z);

        if (!optChunk.isPresent()) {
            MineBot.getLog().severe("Tried to set block of unloaded chunk at " + x + "/" + y + "/" + z);
            return;
        }

        Chunk c = optChunk.get();
//        MineBot.getLog().info("SET BLOCK: " + new BlockType(block).getMaterial().name() + "  " + x + " " + y + " " + z);
        c.setBlockAt(x - (c.getChunkX() * 16), y, z - (c.getChunkZ() * 16), block);
    }

    public Optional<Chunk> getChunkAt(int x, int z) {
        return chunkList.stream()
                .filter(chunk -> x >= (chunk.getChunkX() * 16))
                .filter(chunk -> x < (chunk.getChunkX() * 16 + 16))
                .filter(chunk -> z >= (chunk.getChunkZ() * 16))
                .filter(chunk -> z < (chunk.getChunkZ() * 16 + 16))
                .findAny();
    }

    public void unloadChunk(Chunk chunk) {
        if(chunkList.contains(chunk))
            MineBot.getLog().info("Removed Chunk: " + chunk.getChunkX() + "/" + chunk.getChunkZ());
        else
            MineBot.getLog().info("Could not unload chunk: " + chunk.getChunkX() + "/" + chunk.getChunkZ());
        chunkList.remove(chunk);
    }

    @EventHandler
    public void onBlockChange(BlockChangeEvent event) {
        setBlockAt(event.getX(), event.getY(), event.getZ(), event.getBlock().getBlock());
    }

}
