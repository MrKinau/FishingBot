/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutClickWindow;
import systems.kinau.fishingbot.network.protocol.play.PacketOutClientStatus;
import systems.kinau.fishingbot.network.protocol.play.PacketOutTeleportConfirm;

import java.util.UUID;

public class Player implements Listener {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;

    @Getter @Setter private int experience;
    @Getter @Setter private int levels;

    @Getter @Setter private int heldSlot;
    @Getter @Setter private ByteArrayDataOutput slotData;

    @Getter @Setter private UUID uuid;

    @Getter @Setter private int entityID = -1;
    @Getter @Setter private int lastPing = 500;

    public Player() {
        FishingBot.getInstance().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onPosLookChange(PosLookChangeEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
        this.yaw = event.getYaw();
        this.pitch = event.getPitch();
        if (FishingBot.getInstance().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_9)
            FishingBot.getInstance().getNet().sendPacket(new PacketOutTeleportConfirm(event.getTeleportId()));

    }

    @EventHandler
    public void onUpdateXP(UpdateExperienceEvent event) {
        if(getLevels() >= 0 && getLevels() < event.getLevel()) {
            if(FishingBot.getInstance().getConfig().getAnnounceTypeConsole() != AnnounceType.NONE)
                FishingBot.getLog().info("Achieved level " + event.getLevel());
            if(FishingBot.getInstance().getConfig().isAnnounceLvlUp() && !FishingBot.getInstance().getConfig().getAnnounceLvlUpText().equalsIgnoreCase("false"))
                FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(FishingBot.getInstance().getConfig().getAnnounceLvlUpText().replace("%lvl%", String.valueOf(event.getLevel()))));
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
        if(event.getSlotId() != getHeldSlot())
            return;
        this.slotData = event.getSlotData();
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        setEntityID(event.getEid());
        respawn();
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent event) {
        if (event.getHealth() <= 0)
            respawn();
    }

    @EventHandler
    public void onPingUpdate(PingChangeEvent event) {
        setLastPing(event.getPing());
    }

    public void respawn() {
        FishingBot.getInstance().getNet().sendPacket(new PacketOutClientStatus(PacketOutClientStatus.Action.PERFORM_RESPAWN));
    }

    public void sendMessage(String message) {
        FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(message));
    }

    public void dropStack(short slot, short actionNumber) {
        FishingBot.getInstance().getNet().sendPacket(
                new PacketOutClickWindow(
                        /* player inventory */ 0,
                        slot,
                        /* drop entire stack */ (byte) 1,
                        /* action count starting at 1 */ actionNumber,
                        /* drop entire stack */ 4,
                        /* empty slot */ new Slot(false, -1, (byte) -1, (short) -1, new byte[]{0})
                )
        );
    }
}
