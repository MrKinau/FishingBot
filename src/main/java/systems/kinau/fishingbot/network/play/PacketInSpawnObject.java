/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import java.io.IOException;

public class PacketInSpawnObject extends Packet {

    @Override
    public void write(ByteArrayDataOutput out) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException {
        int id = PacketHelper.readVarInt(in);
        PacketHelper.readUUID(in);
        int type = PacketHelper.readVarInt(in);
        if(type == 101 && networkHandler.getFishingManager().isTrackingNextFishingId()) {
            networkHandler.getFishingManager().setTrackingNextFishingId(false);
            new Thread(() -> {
                try { Thread.sleep(2500); } catch (InterruptedException e) { }     //Prevent Velocity grabbed from flying hook
                networkHandler.getFishingManager().setCurrentBobber(id);
            }).start();
//            FishingBot.getLog().info("Detected new bobber: " + id);
        }
    }
}
