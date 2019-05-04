/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.io.Constants;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInDifficultySet extends Packet {

    @Override
    public void write(ByteArrayDataOutput out) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FishingManager fishingManager = networkHandler.getFishingManager();
            fishingManager.setTrackingNextFishingId(true);
            synchronized (FishingBot.getLog()) {
                FishingBot.getConfig().getStartText().forEach(s -> {
                    networkHandler.sendPacket(new PacketOutChat(s.replace("%prefix%", Constants.PREFIX)));
                });
                networkHandler.sendPacket(new PacketOutUseItem());
                FishingBot.getLog().info("Starting fishing!");
            }
        }).start();
    }
}
