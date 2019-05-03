/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.fishing;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.play.PacketOutUseItem;

public class FishingManager implements Runnable {

    public FishingManager() {
        new Thread(this).start();
    }

    @Getter @Setter private NetworkHandler networkHandler;

    @Getter @Setter private int currentBobber = -1;
    @Getter @Setter private boolean trackingNextFishingId = false;
    @Getter @Setter private boolean trackingNextEntityMeta = false;
    @Getter @Setter long lastFish = System.currentTimeMillis();

    public void fish() {
        setLastFish(System.currentTimeMillis());
        setCurrentBobber(-1);
        setTrackingNextEntityMeta(true);
        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        networkHandler.sendPacket(new PacketOutUseItem());
        new Thread(() -> {
            try {
                Thread.sleep(400);
                setTrackingNextFishingId(true);
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
                networkHandler.sendPacket(new PacketOutUseItem());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {
        while (true) {
            if(System.currentTimeMillis() - getLastFish() > 60000) {
                fish();
                FishingBot.getLog().warning("Bot is slow (Maybe stuck). Trying to restart!");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
