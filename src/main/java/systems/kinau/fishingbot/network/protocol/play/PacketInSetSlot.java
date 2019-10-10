/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSetSlot extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        int window = in.readByte();
        if(window != 0)
            return;
        int slotId = in.readShort();
        if(slotId != PacketInHeldItemChange.getHeldItemSlot())
            return;
        byte[] bytes = new byte[in.getAvailable()];
        in.readBytes(bytes);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(bytes.clone());
        networkHandler.getManager().setSlotData(out);
        ByteArrayDataInputWrapper testFishRod = new ByteArrayDataInputWrapper(bytes.clone());
        if(protocolId < ProtocolConstants.MINECRAFT_1_13) {
            short itemId = testFishRod.readShort();
            if (itemId != 346)  //Normal ID
                noFishingRod(networkHandler);
        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13) {
            short itemId = testFishRod.readShort();
            if (itemId != 563)  //ID in 1.13.0
                noFishingRod(networkHandler);
        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13_1) {
            short itemId = testFishRod.readShort();
            if (itemId != 568)  //ID in 1.13.1
                noFishingRod(networkHandler);
        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13_2) {
            boolean present = testFishRod.readBoolean();
            if(!present)
                noFishingRod(networkHandler);
            int itemId = readVarInt(testFishRod);
            if (itemId != 568) //ID in 1.13.2
                noFishingRod(networkHandler);
        } else {
            boolean present = testFishRod.readBoolean();
            if(!present)
                noFishingRod(networkHandler);
            int itemId = readVarInt(testFishRod);
            if (itemId != 622) //ID in 1.14
                noFishingRod(networkHandler);
        }
    }

    public static void noFishingRod(NetworkHandler networkHandler) {
        networkHandler.sendPacket(new PacketOutChat("Please equip my selected inventory slot with a fishing rod!"));
        MineBot.getLog().severe("No fishing rod equipped. Stopping bot!");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }
}
