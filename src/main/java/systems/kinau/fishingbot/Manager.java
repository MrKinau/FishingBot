package systems.kinau.fishingbot;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosition;

public abstract class Manager {

    @Getter @Setter private NetworkHandler networkHandler;


    private Thread t;

    public abstract void onConnected();

    public void startPositionUpdate(NetworkHandler networkHandler) {
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
