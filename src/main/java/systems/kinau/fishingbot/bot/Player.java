/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.bot;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.actions.BotAction;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.RespawnEvent;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.modules.command.brigardier.argument.MessageArgumentType;
import systems.kinau.fishingbot.modules.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.*;
import systems.kinau.fishingbot.network.protocol.play.PacketOutEntityAction.EntityAction;
import systems.kinau.fishingbot.network.utils.CryptManager;
import systems.kinau.fishingbot.utils.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Player implements Listener {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;
    @Getter @Setter private float originYaw = -255;
    @Getter @Setter private float originPitch = -255;

    @Getter @Setter private int experience;
    @Getter @Setter private int levels;
    @Getter @Setter private float health = -1;
    @Getter @Setter private boolean sentLowHealth;
    @Getter @Setter private boolean respawning;
    @Getter @Setter private boolean sneaking;

    @Getter         private int heldSlot;
    @Getter @Setter private Slot heldItem;
    @Getter @Setter private Inventory inventory;
    @Getter         private final Map<Integer, Inventory> openedInventories = new HashMap<>();
    @Getter @Setter private Optional<CryptManager.MessageSignature> lastUsedSignature = Optional.empty();
    @Getter @Setter private CommandDispatcher<CommandExecutor> mcCommandDispatcher;

    @Getter @Setter private UUID uuid;

    @Getter @Setter private int entityID = -1;
    @Getter @Setter private int lastPing = 500;

    @Getter @Setter private Thread lookThread;

    public Player() {
        this.inventory = new Inventory();
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onPosLookChange(PosLookChangeEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
        this.yaw = event.getYaw();
        this.pitch = event.getPitch();
        if (originYaw == -255 && originPitch == -255) {
            this.originYaw = yaw;
            this.originPitch = pitch;
        }
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_9)
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutTeleportConfirm(event.getTeleportId()));

    }

    @EventHandler
    public void onUpdateXP(UpdateExperienceEvent event) {
        if (getLevels() >= 0 && getLevels() < event.getLevel()) {
            if (FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeConsole() != AnnounceType.NONE)
                FishingBot.getI18n().info("announce-level-up", String.valueOf(event.getLevel()));
            if (FishingBot.getInstance().getCurrentBot().getConfig().isAnnounceLvlUp() && !FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceLvlUpText().equalsIgnoreCase("false"))
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceLvlUpText().replace("%lvl%", String.valueOf(event.getLevel()))));
        }

        this.levels = event.getLevel();
        this.experience = event.getExperience();
    }

    @EventHandler
    public void onSetHeldItem(SetHeldItemEvent event) {
        this.heldSlot = event.getSlot();
    }

    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
        if (event.getWindowId() != 0)
            return;

        Slot slot = event.getSlot();

        if (getInventory() != null)
            getInventory().setItem(event.getSlotId(), slot);

        if (event.getSlotId() == getHeldSlot())
            this.heldItem = slot;
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoLootEjectionEnabled()
                && !(event.getSlotId() == getHeldSlot() && ItemUtils.isFishingRod(slot)))
            FishingBot.getInstance().getCurrentBot().getEjectModule()
                    .executeEjectionRules(FishingBot.getInstance().getCurrentBot().getConfig().getAutoLootEjectionRules(), slot, event.getSlotId());
    }

    @EventHandler
    public void onUpdateWindow(UpdateWindowItemsEvent event) {
        if (event.getWindowId() == 0) {
            for (int i = 0; i < event.getSlots().size(); i++) {
                getInventory().setItem(i, event.getSlots().get(i));
                if (i == getHeldSlot())
                    this.heldItem = event.getSlots().get(i);
                if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoLootEjectionEnabled()
                        && !(i == getHeldSlot() && ItemUtils.isFishingRod(event.getSlots().get(i))))
                    FishingBot.getInstance().getCurrentBot().getEjectModule()
                            .executeEjectionRules(FishingBot.getInstance().getCurrentBot().getConfig().getAutoLootEjectionRules(), event.getSlots().get(i), (short) i);
            }
        } else if (event.getWindowId() > 0) {
            Inventory inventory;
            if (getOpenedInventories().containsKey(event.getWindowId()))
                inventory = getOpenedInventories().get(event.getWindowId());
            else {
                inventory = new Inventory();
                inventory.setWindowId(event.getWindowId());
                getOpenedInventories().put(event.getWindowId(), inventory);
            }
            for (int i = 0; i < event.getSlots().size(); i++)
                inventory.setItem(i, event.getSlots().get(i));
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        getOpenedInventories().remove(event.getWindowId());
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        setEntityID(event.getEid());
        respawn();
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent event) {
        if (event.getEid() != getEntityID())
            return;

        if (getHealth() != -1 && event.getHealth() <= 0 && getEntityID() != -1 && !isRespawning()) {
            setRespawning(true);
            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new RespawnEvent());
            this.sneaking = false;
            respawn();
        } else if (event.getHealth() > 0 && isRespawning())
            setRespawning(false);

        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoCommandBeforeDeathEnabled()) {
            if (event.getHealth() < getHealth() && event.getHealth() <= FishingBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeDeath() && !isSentLowHealth()) {
                for (String command : FishingBot.getInstance().getCurrentBot().getConfig().getAutoCommandBeforeDeath()) {
                    FishingBot.getInstance().getCurrentBot().runCommand(command, true);
                }
                setSentLowHealth(true);
            } else if (isSentLowHealth() && event.getHealth() > FishingBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeDeath())
                setSentLowHealth(false);
        }

        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoQuitBeforeDeathEnabled() && event.getHealth() < getHealth()
                && event.getHealth() <= FishingBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeQuit() && event.getHealth() != 0.0) {
            FishingBot.getI18n().warning("module-fishing-health-threshold-reached");
            FishingBot.getInstance().getCurrentBot().setPreventReconnect(true);
            FishingBot.getInstance().getCurrentBot().setRunning(false);
        }

        this.health = event.getHealth();
    }

    @EventHandler
    public void onRespawn(RespawnEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(FishingBot.getInstance().getCurrentBot().getConfig().getAutoCommandOnRespawnDelay());
            } catch (InterruptedException ignore) { }
            if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoCommandOnRespawnEnabled()) {
                for (String command : FishingBot.getInstance().getCurrentBot().getConfig().getAutoCommandOnRespawn()) {
                    FishingBot.getInstance().getCurrentBot().runCommand(command, true);
                }
            }
        }).start();
    }

    @EventHandler
    public void onPingUpdate(PingChangeEvent event) {
        setLastPing(event.getPing());
    }

    @EventHandler
    public void onCommandsRegistered(CommandsRegisteredEvent event) {
        setMcCommandDispatcher(event.getCommandDispatcher());
    }

    public void respawn() {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutClientStatus(PacketOutClientStatus.Action.PERFORM_RESPAWN));

        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoSneak()) {
            FishingBot.getScheduler().schedule(() -> {
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutEntityAction(EntityAction.START_SNEAKING));
                this.sneaking = true;
            }, 250, TimeUnit.MILLISECONDS);
        }
    }

    public void sendMessage(String message) {
        message = message.replace("%prefix%", FishingBot.PREFIX);
        for (String line : message.split("\n")) {
            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.MINECRAFT_1_8) {
                for (String split : StringUtils.splitDescription(line)) {
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(split));
                }
            } else if (FishingBot.getInstance().getCurrentBot().getServerProtocol() < ProtocolConstants.MINECRAFT_1_19) {
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(line));
            } else {
                if (line.startsWith("/"))
                    executeChatCommand(line.substring(1));
                else
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(line));
            }
        }
    }

    private void executeChatCommand(String command) {
        if (mcCommandDispatcher == null) {
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatCommand(command));
            return;
        }

        CommandContextBuilder<CommandExecutor> context = mcCommandDispatcher.parse(command, CommandExecutor.UNSET).getContext();
        Map<String, Pair<ArgumentType<?>, ParsedArgument<CommandExecutor, ?>>> arguments = CommandUtils.getArguments(context);
        boolean containsSignableArguments = arguments.values().stream().anyMatch(argument -> argument.getKey() instanceof MessageArgumentType);
        if (!containsSignableArguments) {
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatCommand(command));
            return;
        }
        List<CryptManager.SignableArgument> signableArguments = arguments.entrySet().stream()
                .filter(entry -> entry.getValue().getKey() instanceof MessageArgumentType)
                .map(entry -> new CryptManager.SignableArgument(entry.getKey(), entry.getValue().getValue().getResult().toString()))
                .collect(Collectors.toList());
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatCommand(command, signableArguments));
    }

    public void dropStack(short slot, short actionNumber) {
        Map<Short, Slot> remainingSlots = new HashMap<>();
        remainingSlots.put(slot, Slot.EMPTY);
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                new PacketOutClickWindow(
                        /* player inventory */ 0,
                        slot,
                        /* drop entire stack */ (byte) 1,
                        /* action count starting at 1 */ actionNumber,
                        /* drop entire stack */ 4,
                        /* empty slot */ Slot.EMPTY,
                        remainingSlots
                )
        );

        FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().setItem(slot, Slot.EMPTY);
    }

    public void swapToHotBar(int slotId, int hotBarButton) {
        // This is not notchian behaviour, but it works
        Map<Short, Slot> remainingSlots = new HashMap<>();
        remainingSlots.put((short) slotId, Slot.EMPTY);
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                new PacketOutClickWindow(
                        /* player inventory */ 0,
                        /* the clicked slot */ (short) slotId,
                        /* use hotBar Button */ (byte) hotBarButton,
                        /* action count starting at 1 */ (short) 1,
                        /* hotBar button mode */ 2,
                        /* slot */ getInventory().getContent().get(slotId),
                        remainingSlots
                )
        );
        try { Thread.sleep(20); } catch (InterruptedException ignore) { }
        closeInventory();

        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() <= ProtocolConstants.MINECRAFT_1_17) {
            Slot slot = FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(slotId);
            FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().put(slotId, FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(hotBarButton + 36));
            FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().put(hotBarButton + 36, slot);
        }
    }

    public void shiftToInventory(int slotId, Inventory inventory) {
        // This is not notchian behaviour, but it works
        Map<Short, Slot> remainingSlots = new HashMap<>();
        remainingSlots.put((short) slotId, Slot.EMPTY);
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                new PacketOutClickWindow(
                        /* player inventory */ inventory.getWindowId(),
                        /* the clicked slot */ (short) (slotId + (inventory.getContent().size() == 63 ? 18 : 45)),
                        /* use right click */ (byte) 0,
                        /* action count starting at 1 */ inventory.getActionCounter(),
                        /* shift click mode */ 1,
                        /* slot */ getInventory().getContent().get(slotId),
                        remainingSlots
                )
        );
        try { Thread.sleep(20); } catch (InterruptedException ignore) { }

        FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().put(slotId, Slot.EMPTY);
    }

    public boolean look(LocationUtils.Direction direction, Consumer<Boolean> onFinish) {
        float yaw = direction.getYaw() == Float.MIN_VALUE ? getYaw() : direction.getYaw();
        float pitch = direction.getPitch() == Float.MIN_VALUE ? getPitch() : direction.getPitch();
        return look(yaw, pitch, FishingBot.getInstance().getCurrentBot().getConfig().getLookSpeed(), onFinish);
    }

    public boolean look(float yaw, float pitch, int speed) {
        return look(yaw, pitch, speed, null);
    }

    public boolean look(float yaw, float pitch, int speed, Consumer<Boolean> onFinish) {
        if (lookThread != null && Thread.currentThread().getId() != lookThread.getId() && lookThread.isAlive()) {
            return false;
        } else if (lookThread != null && Thread.currentThread().getId() == lookThread.getId() && lookThread.isAlive()) {
            internalLook(yaw, pitch, speed, onFinish); // calling look inside onFinish
            return true;
        }

        this.lookThread = new Thread(() -> {
            internalLook(yaw, pitch, speed, onFinish);
        });
        getLookThread().start();
        return true;
    }

    private void internalLook(float yaw, float pitch, int speed, Consumer<Boolean> onFinish) {
        float yawDiff = LocationUtils.yawDiff(getYaw(), yaw);
        float pitchDiff = LocationUtils.yawDiff(getPitch(), pitch);

        int steps = (int) Math.ceil(Math.max(Math.abs(yawDiff), Math.abs(pitchDiff)) / Math.max(1, speed));
        float yawPerStep = yawDiff / steps;
        float pitchPerStep = pitchDiff / steps;

        for (int i = 0; i < steps; i++) {
            setYaw(getYaw() + yawPerStep);
            setPitch(getPitch() + pitchPerStep);
            if (getYaw() > 180)
                setYaw(-180 + (getYaw() - 180));
            if (getYaw() < -180)
                setYaw(180 + (getYaw() + 180));
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), true));
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) { }
        }
        if (onFinish != null)
            onFinish.accept(true);

        try {
            Thread.sleep(50);
        } catch (InterruptedException ignore) { }
    }

    public boolean isCurrentlyLooking() {
        return !(lookThread == null || lookThread.isInterrupted() || !lookThread.isAlive());
    }

    public void openAdjacentChest(LocationUtils.Direction direction) {
        int x = (int)Math.floor(getX());
        int y = (int)Math.round(getY());
        int z = (int)Math.floor(getZ());
        PacketOutBlockPlace.BlockFace blockFace;
        switch (direction) {
            case EAST: x++; blockFace = PacketOutBlockPlace.BlockFace.WEST; break;
            case WEST: x--; blockFace = PacketOutBlockPlace.BlockFace.EAST; break;
            case NORTH: z--; blockFace = PacketOutBlockPlace.BlockFace.SOUTH; break;
            case DOWN: y--; blockFace = PacketOutBlockPlace.BlockFace.TOP; break;
            default: z++; blockFace = PacketOutBlockPlace.BlockFace.NORTH; break;
        }
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.MINECRAFT_1_8) {
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem(
                    x, y, z, (byte)0, (byte)0, (byte)0, blockFace
            ));
        } else {
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutBlockPlace(
                    PacketOutBlockPlace.Hand.MAIN_HAND,
                    x, y, z, blockFace,
                    0.5F, 0.5F, 0.5F,
                    false
            ));
        }
    }

    public void closeInventory() {
        closeInventory(0);
    }

    public void closeInventory(int windowId) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutCloseInventory(windowId));
    }

    public void setHeldSlot(int heldSlot) {
        setHeldSlot(heldSlot, true);
    }

    public void setHeldSlot(int heldSlot, boolean sendPacket) {
        if (sendPacket)
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutHeldItemChange(heldSlot));
        this.heldSlot = heldSlot;
    }

    public void use() {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
    }

    public boolean executeBotAction(BotAction botAction) {
        return botAction.execute(FishingBot.getInstance().getCurrentBot());
    }
}
