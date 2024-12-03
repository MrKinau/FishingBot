/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.bot.registry.MetaRegistry;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.bot.registry.RegistryLoader;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.common.KeepAliveEvent;
import systems.kinau.fishingbot.event.common.PingPacketEvent;
import systems.kinau.fishingbot.event.common.ResourcePackEvent;
import systems.kinau.fishingbot.event.configuration.ConfigurationStartEvent;
import systems.kinau.fishingbot.event.configuration.RegistryDataEvent;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.modules.command.executor.ConsoleCommandExecutor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.ProtocolState;
import systems.kinau.fishingbot.network.protocol.common.PacketOutClientSettings;
import systems.kinau.fishingbot.network.protocol.common.PacketOutKeepAlive;
import systems.kinau.fishingbot.network.protocol.common.PacketOutPing;
import systems.kinau.fishingbot.network.protocol.common.PacketOutResourcePackResponse;
import systems.kinau.fishingbot.network.protocol.play.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class ClientDefaultsModule extends Module implements Listener {

    private Thread positionThread;
    private boolean joined;
    private Set<UUID> onlinePlayers = new HashSet<>();

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
        if (FishingBot.getInstance().getCurrentBot().getNet().isEncrypted()
                && FishingBot.getInstance().getCurrentBot().getAuthData().getProfileKeys() != null
                && FishingBot.getInstance().getCurrentBot().getServerProtocol() > ProtocolConstants.MC_1_19_1) {
            AuthData.ProfileKeys keys = FishingBot.getInstance().getCurrentBot().getAuthData().getProfileKeys();
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatSessionUpdate(keys));
        }
        if (isJoined())
            return;
        this.joined = true;
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) { }

            // Send start texts
            if (FishingBot.getInstance().getCurrentBot().getConfig().isStartTextEnabled()) {
                FishingBot.getInstance().getCurrentBot().getConfig().getStartText().forEach(s -> {
                    FishingBot.getInstance().getCurrentBot().runCommand(s, true, new ConsoleCommandExecutor());
                });
            }

            // Start position updates
            startPositionUpdate(FishingBot.getInstance().getCurrentBot().getNet());
        }).start();
    }

    @EventHandler
    public void onDisconnect(DisconnectEvent event) {
        FishingBot.getI18n().info("module-client-disconnected", event.getDisconnectMessage());
        FishingBot.getInstance().getCurrentBot().setRunning(false);
        onlinePlayers.clear();
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        onlinePlayers.clear();
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutClientSettings());
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_21_4)
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPlayerLoaded());
    }

    @EventHandler
    public void onKeepAlive(KeepAliveEvent event) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutKeepAlive(event.getId()));
    }

    @EventHandler
    public void onResourcePack(ResourcePackEvent event) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutResourcePackResponse(event.getUuid(), PacketOutResourcePackResponse.Result.ACCEPTED));
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutResourcePackResponse(event.getUuid(), PacketOutResourcePackResponse.Result.SUCCESSFULLY_LOADED));
    }

    @EventHandler
    public void onUpdatePlayerList(UpdatePlayerListEvent event) {
        switch (event.getAction()) {
            case REPLACE: {
                onlinePlayers = event.getPlayers();
                break;
            }
            case ADD: {
                onlinePlayers.addAll(event.getPlayers());
                break;
            }
            case REMOVE: {
                onlinePlayers.removeAll(event.getPlayers());
                break;
            }
        }
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoDisconnect() && onlinePlayers.size() > FishingBot.getInstance().getCurrentBot().getConfig().getAutoDisconnectPlayersThreshold()) {
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

    @EventHandler
    public void onOpenWindow(OpenWindowEvent e) {
        FishingBot.getI18n().info("log-inventory-opened", e.getTitle());
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if (positionThread != null)
            positionThread.interrupt();
        positionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Player player = FishingBot.getInstance().getCurrentBot().getPlayer();
                if (networkHandler != null && networkHandler.getState() == ProtocolState.PLAY)
                    networkHandler.sendPacket(new PacketOutPosLook(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), true, true));
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
        });
        positionThread.setName("positionThread");
        positionThread.start();
    }

    @EventHandler
    public void onConfigurationStart(ConfigurationStartEvent e) {
        if (positionThread != null)
            positionThread.interrupt();
    }

    @EventHandler
    public void onPing(PingPacketEvent e) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPing(e.getId()));
    }

    @EventHandler
    public void onEntityData(EntityDataEvent event) {
        event.getData().stream()
                .filter(element -> element.getElement().getInternalId().equals("float"))
                .forEach(element -> {
                    int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();
                    if ((protocolId < ProtocolConstants.MC_1_10 && element.getElementIndex() == 6)
                            || (protocolId < ProtocolConstants.MC_1_14_4 && element.getElementIndex() == 7)
                            || (protocolId < ProtocolConstants.MC_1_17 && element.getElementIndex() == 8)
                            || (protocolId >= ProtocolConstants.MC_1_17 && element.getElementIndex() == 9)) {
                        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(event.getEntityId(), (float) element.getElement().getValue(), -1 ,-1));
                    }
                });
    }

    @EventHandler
    public void onRegistryData(RegistryDataEvent event) {
        MetaRegistry<Integer, String> metaRegistry = Registries.getByIdentifier(event.getRegistryId(), FishingBot.getInstance().getCurrentBot().getServerProtocol());
        if (metaRegistry == null) return;
        metaRegistry.load(event.getRegistryData(), RegistryLoader.mapped(), FishingBot.getInstance().getCurrentBot().getServerProtocol());
    }

    @EventHandler
    public void onChunkBatchFinished(ChunkBatchFinishedEvent event) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChunkBatchReceived(20));
    }
}
