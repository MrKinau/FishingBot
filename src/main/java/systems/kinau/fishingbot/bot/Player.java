/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.bot;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.RespawnEvent;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.*;
import systems.kinau.fishingbot.network.protocol.play.PacketOutEntityAction.EntityAction;
import systems.kinau.fishingbot.network.utils.LocationUtils;
import systems.kinau.fishingbot.network.utils.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Player implements Listener {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;

    @Getter @Setter private int experience;
    @Getter @Setter private int levels;
    @Getter @Setter private float health = -1;
    @Getter @Setter private boolean sentLowHealth;
    @Getter @Setter private boolean respawning;
    @Getter @Setter private boolean sneaking;

    @Getter @Setter private int heldSlot;
    @Getter @Setter private Slot heldItem;
    @Getter @Setter private Inventory inventory;

    @Getter @Setter private UUID uuid;

    @Getter @Setter private int entityID = -1;
    @Getter @Setter private int lastPing = 500;

    @Getter @Setter private Thread lookThread;

    public Player() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onPosLookChange(PosLookChangeEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
        this.yaw = event.getYaw();
        this.pitch = event.getPitch();
        this.inventory = new Inventory();
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_9)
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutTeleportConfirm(event.getTeleportId()));

    }

    @EventHandler
    public void onUpdateXP(UpdateExperienceEvent event) {
        if (getLevels() >= 0 && getLevels() < event.getLevel()) {
            if (FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeConsole() != AnnounceType.NONE)
                FishingBot.getI18n().info("announce-level-up", String.valueOf(event.getLevel()));
            if (FishingBot.getInstance().getCurrentBot().getConfig().isAnnounceLvlUp() && !FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceLvlUpText().equalsIgnoreCase("false"))
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChat(FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceLvlUpText().replace("%lvl%", String.valueOf(event.getLevel()))));
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
        if(event.getWindowId() != 0)
            return;

        Slot slot = event.getSlot();

        if (getInventory() != null) {
            getInventory().setItem(event.getSlotId(), slot);
        }

        if(event.getSlotId() == getHeldSlot())
            this.heldItem = slot;
    }

    @EventHandler
    public void onUpdateWindow(UpdateWindowItemsEvent event) {
        if (event.getWindowId() != 0)
            return;

        for (int i = 0; i < event.getSlots().size(); i++) {
            getInventory().setItem(i, event.getSlots().get(i));
            if(i == getHeldSlot())
                this.heldItem = event.getSlots().get(i);
        }
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
                    sendMessage(command.replace("%prefix%", FishingBot.PREFIX));
                }
                setSentLowHealth(true);
            } else if (isSentLowHealth() && event.getHealth() > FishingBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeDeath())
                setSentLowHealth(false);
        }

        if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoQuitBeforeDeathEnabled()) {
            if (event.getHealth() < getHealth() && event.getHealth() <= FishingBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeQuit()) {
                FishingBot.getI18n().warning("module-fishing-health-threshold-reached");
                System.exit(0);
            }
        }

        this.health = event.getHealth();
    }

    @EventHandler
    public void onRespawn(RespawnEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(FishingBot.getInstance().getCurrentBot().getConfig().getAutoCommandOnRespawnDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (FishingBot.getInstance().getCurrentBot().getConfig().isAutoCommandOnRespawnEnabled()) {
                for (String command : FishingBot.getInstance().getCurrentBot().getConfig().getAutoCommandOnRespawn()) {
                    sendMessage(command.replace("%prefix%", FishingBot.PREFIX));
                }
            }
        }).start();
    }

    @EventHandler
    public void onPingUpdate(PingChangeEvent event) {
        setLastPing(event.getPing());
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
        for (String line : message.split("\n")) {
            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.MINECRAFT_1_8) {
                for (String split : StringUtils.splitDescription(line)) {
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChat(split));
                }
            } else {
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChat(line));
            }
        }
    }

    public void dropStack(short slot, short actionNumber) {
        Slot emptySlot = new Slot(false, -1, (byte) -1, (short) -1, new byte[]{0});

        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                new PacketOutClickWindow(
                        /* player inventory */ 0,
                        slot,
                        /* drop entire stack */ (byte) 1,
                        /* action count starting at 1 */ actionNumber,
                        /* drop entire stack */ 4,
                        /* empty slot */ emptySlot
                )
        );

        FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().setItem(slot, emptySlot);
    }

    public void swapToHotBar(int slotId, int hotBarButton) {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                new PacketOutClickWindow(
                        /* player inventory */ 0,
                        /* the clicked slot */ (short) slotId,
                        /* use hotBar Button */ (byte) hotBarButton,
                        /* action count starting at 1 */ (short) 1,
                        /* hotBar button mode */ 2,
                        /* slot */ getInventory().getContent().get(slotId)
                )
        );
        try { Thread.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutCloseInventory(0));

        Slot slot = FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(slotId);
        FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().put(slotId, FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(hotBarButton + 36));
        FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().put(hotBarButton + 36, slot);
    }

    public void look(LocationUtils.Direction direction, Consumer<Boolean> onFinish) {
        look(direction.getYaw(), getPitch(), 8, onFinish);
    }

    public void look(float yaw, float pitch, int speed) {
        look(yaw, pitch, speed, null);
    }

    public void look(float yaw, float pitch, int speed, Consumer<Boolean> onFinish) {
        if (lookThread != null && lookThread.isAlive())
            lookThread.interrupt();

        this.lookThread = new Thread(() -> {
            float yawDiff = LocationUtils.yawDiff(getYaw(), yaw);
            float pitchDiff = LocationUtils.yawDiff(getPitch(), pitch);

            int steps = (int) Math.ceil(Math.max(Math.abs(yawDiff), Math.abs(pitchDiff)) / Math.max(1, speed));
            float yawPerStep = yawDiff / steps;
            float pitchPerStep = pitchDiff / steps;

            for (int i = 0; i < steps; i++) {
                if (lookThread.isInterrupted()) {
                    if (onFinish != null)
                        onFinish.accept(false);
                    break;
                }
                setYaw(getYaw() + yawPerStep);
                setPitch(getPitch() + pitchPerStep);
                if (getYaw() > 180)
                    setYaw(-180 + (getYaw() - 180));
                if (getYaw() < -180)
                    setYaw(180 + (getYaw() + 180));
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutPosLook(getX(), getY(), getZ(), getYaw(), getPitch(), true));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (onFinish != null)
                onFinish.accept(true);
        });
        getLookThread().start();
    }
}
