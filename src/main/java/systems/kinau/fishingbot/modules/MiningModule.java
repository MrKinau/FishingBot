/*
 * Created by David Luedtke (MrKinau)
 * 2019/11/2
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.JoinGameEvent;
import systems.kinau.fishingbot.mining.World;

public class MiningModule extends Module implements Listener {

    @Override
    public void onEnable() {
        MineBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() { }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        MineBot.getInstance().setWorld(new World(event.getDimension(), event.getLevelType()));
        MineBot.getInstance().getPlayer().startMining();
        new Thread(() -> {
            while (!Thread.interrupted()) {
                MineBot.getInstance().getPlayer().tick();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
