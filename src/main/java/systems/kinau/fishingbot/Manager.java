package systems.kinau.fishingbot;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;

public abstract class Manager {

    @Getter @Setter private NetworkHandler networkHandler;

    private Thread t;

    public abstract void onConnected();

    public abstract void tick();

    public void startPositionUpdate(NetworkHandler networkHandler) {
        if(t != null)
            t.interrupt();
        t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                tick();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void shutdown() {
        t.interrupt();
    }
}
