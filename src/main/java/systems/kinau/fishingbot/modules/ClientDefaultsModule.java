/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.play.PacketOutClientSettings;
import systems.kinau.fishingbot.network.protocol.play.PacketOutConfirmTransaction;
import systems.kinau.fishingbot.network.protocol.play.PacketOutKeepAlive;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosLook;

public class ClientDefaultsModule extends Module implements Listener {

    @Getter private Thread positionThread;
    @Getter private boolean joined;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        if (getPositionThread() != null)
            getPositionThread().interrupt();
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        if (isJoined())
            return;
        this.joined = true;
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) { }

            //Send start texts
            if (FishingBot.getInstance().getCurrentBot().getConfig().isStartTextEnabled()) {
                FishingBot.getInstance().getCurrentBot().getConfig().getStartText().forEach(s -> {
                    FishingBot.getInstance().getCurrentBot().getPlayer().sendMessage(s);
                });
            }

            //Start position updates
            startPositionUpdate(FishingBot.getInstance().getCurrentBot().getNet());
        }).start();
    }

    @EventHandler
    public void onDisconnect(DisconnectEvent event) {
        FishingBot.getI18n().info("module-client-disconnected", event.getDisconnectMessage());
        FishingBot.getInstance().getCurrentBot().setRunning(false);
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutClientSettings());
    }

    @EventHandler
    public void onKeepAlive(KeepAliveEvent event) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutKeepAlive(event.getId()));
    }

    @EventHandler
    public void onUpdatePlayerList(UpdatePlayerListEvent event) {
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoDisconnect() && event.getPlayers().size() > FishingBot.getInstance().getCurrentBot().getConfig().getAutoDisconnectPlayersThreshold()) {
            FishingBot.getI18n().warning("network-server-is-full");
            FishingBot.getInstance().getCurrentBot().setWontConnect(true);
            FishingBot.getInstance().getCurrentBot().setRunning(false);
        }
    }

    @EventHandler
    public void onConfirmTransaction(ConfirmTransactionEvent event) {
        if (!event.isAccepted())
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutConfirmTransaction(event.getWindowId(), event.getAction(), true));
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if (positionThread != null)
            positionThread.interrupt();
        positionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Player player = FishingBot.getInstance().getCurrentBot().getPlayer();
                networkHandler.sendPacket(new PacketOutPosLook(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), true));
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
        });
        positionThread.setName("positionThread");
        positionThread.start();
    }
}
