package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class PacketInChunk extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int x = in.readInt();
        int z = in.readInt();
        boolean fullChunk = in.readBoolean();
        int bitmask = in.readUnsignedShort();
        int size = readVarInt(in);

//        byte[] test = new byte[size];
//        in.readBytes(test);
        List<Integer> testList = new ArrayList<>();
//        for (byte b : test) testList.add(b);

        for (int sectionY = 0; sectionY < 16; sectionY++) {
            if (((bitmask & (1 << sectionY)) != 0)) {


                MineBot.getLog().info("Loaded chunk section from " + sectionY * 16 + " to " + (sectionY * 16 + 15));
                MineBot.getLog().info(testList.toString());
                testList = new ArrayList<>();
            }
        }
        MineBot.getLog().info("Loaded Chunk at: " + (x * 16) + "/" + (z * 16) + ": " + bitmask + "->" + Integer.toBinaryString(bitmask) + " (all: " + fullChunk + ")");
    }

}
