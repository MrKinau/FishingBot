/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.play.*;

import java.util.Arrays;

public class ClientDefaultsModule extends Module implements Listener {

    @Getter
    private Thread positionThread;

    @Override
    public void onEnable() {
        MineBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        positionThread.interrupt();
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Send start texts
            if (MineBot.getInstance().getConfig().isStartTextEnabled()) {
                Arrays.asList(MineBot.getInstance().getConfig().getStartText().split(";")).forEach(s -> {
                    MineBot.getInstance().getNet().sendPacket(new PacketOutChat(s.replace("%prefix%", MineBot.PREFIX)));
                });
            }

            //Start position updates
            startPositionUpdate(MineBot.getInstance().getNet());
        }).start();
    }

    @EventHandler
    public void onDisconnect(DisconnectEvent event) {
        MineBot.getLog().info("Disconnected: " + event.getDisconnectMessage());
        MineBot.getInstance().setRunning(false);
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        MineBot.getInstance().getNet().sendPacket(new PacketOutClientSettings());
    }

    @EventHandler
    public void onKeepAlive(KeepAliveEvent event) {
        MineBot.getInstance().getNet().sendPacket(new PacketOutKeepAlive(event.getId()));
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent event) {
        if (event.getHealth() <= 0) {
            MineBot.getInstance().getNet().sendPacket(new PacketOutClientStatus(0));
        }
    }

    @EventHandler
    public void onUpdatePlayerList(UpdatePlayerListEvent event) {
        if (MineBot.getInstance().getConfig().isAutoDisconnect() && event.getPlayers().size() > MineBot.getInstance().getConfig().getAutoDisconnectPlayersThreshold()) {
            MineBot.getLog().warning("Max players threshold reached. Stopping");
            MineBot.getInstance().setWontConnect(true);
            MineBot.getInstance().setRunning(false);
        }
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if (positionThread != null)
            positionThread.interrupt();
        positionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Player player = MineBot.getInstance().getPlayer();
                networkHandler.sendPacket(new PacketOutPosition(player.getX(), player.getY(), player.getZ(), true));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        positionThread.start();
    }
}
