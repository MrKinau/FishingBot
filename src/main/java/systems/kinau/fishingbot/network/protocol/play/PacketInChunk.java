package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.mining.Chunk;
import systems.kinau.fishingbot.mining.World;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.Optional;

@NoArgsConstructor
public class PacketInChunk extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int chunkX = in.readInt();
        int chunkZ = in.readInt();
        boolean fullChunk = in.readBoolean();
        int bitmask = in.readUnsignedShort();
        readVarInt(in); //size (unimportant for us, I think)

        World world = MineBot.getInstance().getWorld();
        Optional<Chunk> optChunk = world.getChunk(chunkX, chunkZ);
        Chunk chunk;
        if(!optChunk.isPresent()) {
            chunk = new Chunk(chunkX, chunkZ, bitmask);
            world.addChunk(chunk);
        } else
            chunk = optChunk.get();

        chunk.loadSections(in, world.getDimension() == 0, fullChunk);
    }

}
