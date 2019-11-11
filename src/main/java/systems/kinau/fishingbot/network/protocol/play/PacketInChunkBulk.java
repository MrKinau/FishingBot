/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.mining.Chunk;
import systems.kinau.fishingbot.mining.World;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacketInChunkBulk extends Packet {
    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        boolean lightning = in.readBoolean();
        int chunkCount = readVarInt(in);

        World world = MineBot.getInstance().getWorld();
        List<Chunk> chunks = new ArrayList<>();

        for(int i = 0; i < chunkCount; i++) {
            int chunkX = in.readInt();
            int chunkZ = in.readInt();
            int bitmask = in.readUnsignedShort();

            Optional<Chunk> optChunk = world.getChunk(chunkX, chunkZ);
            Chunk chunk;
            if(!optChunk.isPresent()) {
                chunk = new Chunk(chunkX, chunkZ, bitmask);
                world.addChunk(chunk);
            } else
                chunk = optChunk.get();
            chunks.add(chunk);
        }

        chunks.forEach(chunk -> chunk.loadSections(in, lightning, true));
    }
}
