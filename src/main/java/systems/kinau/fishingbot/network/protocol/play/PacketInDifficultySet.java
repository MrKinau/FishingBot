/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInDifficultySet extends Packet {

    private Thread t;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FishingManager fishingManager = networkHandler.getFishingManager();
            fishingManager.setTrackingNextFishingId(true);
            synchronized (FishingBot.getLog()) {
                FishingBot.getConfig().getStartText().forEach(s -> {
                    networkHandler.sendPacket(new PacketOutChat(s.replace("%prefix%", FishingBot.PREFIX)));
                });
                networkHandler.sendPacket(new PacketOutUseItem(networkHandler));
                FishingBot.getLog().info("Starting fishing!");
                if(FishingBot.getServerProtocol() == ProtocolConstants.MINECRAFT_1_8)
                    startPositionUpdate(networkHandler);
            }
        }).start();
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if(t != null)
            t.interrupt();
        t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                networkHandler.sendPacket(new PacketOutPosition());
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
