/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.PosLookChangeEvent;
import systems.kinau.fishingbot.event.play.SetHeldItemEvent;
import systems.kinau.fishingbot.event.play.UpdateExperienceEvent;
import systems.kinau.fishingbot.event.play.UpdateSlotEvent;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.mining.*;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.*;

public class Player implements Listener {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;
    @Getter @Setter private boolean onGround = true;

    @Getter @Setter private Vector velocity = new Vector(0, 0, 0);
    @Getter private Vector oneTickVelocity = new Vector(0, 0, 0);

    @Getter @Setter private int experience;
    @Getter @Setter private int levels;

    @Getter @Setter private int heldSlot;
    @Getter @Setter private ByteArrayDataOutput slotData;

    @Getter @Setter private boolean resetY = false;

    public Player() {
        MineBot.getInstance().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onPosLookChange(PosLookChangeEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
        this.yaw = event.getYaw();
        this.pitch = event.getPitch();
        if (MineBot.getInstance().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_9)
            MineBot.getInstance().getNet().sendPacket(new PacketOutTeleportConfirm(event.getTeleportId()));

    }

    @EventHandler
    public void onUpdateXP(UpdateExperienceEvent event) {
        if(getLevels() >= 0 && getLevels() < event.getLevel()) {
            if(MineBot.getInstance().getConfig().getAnnounceTypeConsole() != AnnounceType.NONE)
                MineBot.getLog().info("Achieved level " + event.getLevel());
            if(!MineBot.getInstance().getConfig().getAnnounceLvlUp().equalsIgnoreCase("false"))
                MineBot.getInstance().getNet().sendPacket(new PacketOutChat(MineBot.getInstance().getConfig().getAnnounceLvlUp().replace("%lvl%", String.valueOf(event.getLevel()))));
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

    public void onGroundCheck() {
        World world = MineBot.getInstance().getWorld();
        if (world != null) {
            BlockType bt = world.getBlockAt(Double.valueOf(Math.floor(x - getVelocity().getXV())).intValue(), Double.valueOf(Math.floor((y - getVelocity().getYV()) - 1)).intValue(), Double.valueOf(Math.floor(z - getVelocity().getZV())).intValue());
            if (!bt.getMaterial().isSolid() && isOnGround())
                setOnGround(false);
        }
    }

    public boolean testWallCollision() {
        World world = MineBot.getInstance().getWorld();
        if (world != null) {
            BlockType bt = world.getBlockAt(Double.valueOf(Math.floor(x + (4 * getVelocity().getXV()))).intValue(), Double.valueOf(Math.floor(y)).intValue(), Double.valueOf(Math.floor(z)).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setXV(0);
                return true;
            }
            bt = world.getBlockAt(Double.valueOf(Math.floor(x)).intValue(), Double.valueOf(Math.floor(y)).intValue(), Double.valueOf(Math.floor(z + (4 * getVelocity().getZV()))).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setZV(0);
                return true;
            }
            bt = world.getBlockAt(Double.valueOf(Math.floor(x + (4 * getVelocity().getXV()))).intValue(), Double.valueOf(Math.floor(y)).intValue() + 1, Double.valueOf(Math.floor(z)).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setXV(0);
                return true;
            }
            bt = world.getBlockAt(Double.valueOf(Math.floor(x)).intValue(), Double.valueOf(Math.floor(y)).intValue() + 1, Double.valueOf(Math.floor(z + (4 * getVelocity().getZV()))).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setZV(0);
                return true;
            }
        }
        return false;
    }

    public boolean testWallCollision(byte direction) {
        float xMov = 0;
        float zMov = 0;
        switch (direction) {
            case BlockFace.X_POSITIVE: xMov = 0.15F; break;
            case BlockFace.Z_NEGATIVE: zMov = -0.15F; break;
            case BlockFace.X_NEGATIVE: xMov = -0.15F; break;
            case BlockFace.Z_POSITIVE: zMov = 0.15F; break;
        }
        World world = MineBot.getInstance().getWorld();
        if (world != null) {
            BlockType bt = world.getBlockAt(Double.valueOf(Math.floor(x + (4 * xMov))).intValue(), Double.valueOf(Math.floor(y)).intValue(), Double.valueOf(Math.floor(z)).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setXV(0);
                return true;
            }
            bt = world.getBlockAt(Double.valueOf(Math.floor(x)).intValue(), Double.valueOf(Math.floor(y)).intValue(), Double.valueOf(Math.floor(z + (4 * zMov))).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setZV(0);
                return true;
            }
            bt = world.getBlockAt(Double.valueOf(Math.floor(x + (4 * xMov))).intValue(), Double.valueOf(Math.floor(y)).intValue() + 1, Double.valueOf(Math.floor(z)).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setXV(0);
                return true;
            }
            bt = world.getBlockAt(Double.valueOf(Math.floor(x)).intValue(), Double.valueOf(Math.floor(y)).intValue() + 1, Double.valueOf(Math.floor(z + (4 * zMov))).intValue());
            if(bt.getMaterial().isSolid()) {
                getVelocity().setZV(0);
                return true;
            }
        }
        return false;
    }

    public void applyGravity() {
        if(isOnGround())
            return;
        if(getVelocity().getYV() > -3.92)
            getVelocity().addY(-0.08F * 0.98F);
        World world = MineBot.getInstance().getWorld();
        if (world != null) {
            double yTest = getVelocity().getYV();
            while (yTest < 0) {
                BlockType bt = world.getBlockAt(Double.valueOf(Math.floor(x)).intValue(), Double.valueOf(Math.floor(y + yTest)).intValue(), Double.valueOf(Math.floor(z)).intValue());
                if (bt.getMaterial().isSolid()) {
                    getVelocity().setYV(-(y - Double.valueOf(Math.floor(y)).intValue()));
                    setOnGround(true);
                    this.resetY = true;
                }
                yTest++;
            }
        }
    }

    public void permaMove(double x, double y, double z) {
        if (x != 0) getVelocity().setXV(x);
        if (y != 0) getVelocity().setYV(y);
        if (z != 0) getVelocity().setZV(z);
    }

    public void addOneTickMotion(double x, double y, double z) {
        getOneTickVelocity().addX(x);
        getOneTickVelocity().addY(y);
        getOneTickVelocity().addZ(z);
    }

    public void addMotion(double x, double y, double z) {
        getVelocity().addX(x);
        getVelocity().addY(y);
        getVelocity().addZ(z);
    }

    public void dig(byte digStatus, int x, int y, int z, byte blockFace) {
        MineBot.getInstance().getNet().sendPacket(new PacketOutDig(digStatus, x, y, z, blockFace));
        MineBot.getInstance().getNet().sendPacket(new PacketOutArmAnimation());
    }

    public void place(int slot, int x, int y, int z, byte blockFace) {
        System.out.println("PLACE BLOCK AT: " + x + " " + y + " " + z);
        MineBot.getInstance().getNet().sendPacket(new PacketOutHeldItemChange((short)slot));
        MineBot.getInstance().getNet().sendPacket(new PacketOutBlockPlacement(x, y, z, blockFace, getSlotData().toByteArray(), (byte)0, (byte)0, (byte)0));
        MineBot.getInstance().getNet().sendPacket(new PacketOutArmAnimation());
        MineBot.getInstance().getNet().sendPacket(new PacketOutHeldItemChange((short)0));
    }

    public void startMining() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StripMining stripMining = new StripMining();
        }).start();
    }

    public void tick() {
        addMotion(getOneTickVelocity().getYV(), getOneTickVelocity().getYV(), getOneTickVelocity().getZV());
        onGroundCheck();
        if(!isOnGround())
            applyGravity();

        x += getVelocity().getXV();
        y += getVelocity().getYV();
        z += getVelocity().getZV();

        testWallCollision();

        addMotion(-getOneTickVelocity().getYV(), -getOneTickVelocity().getYV(), -getOneTickVelocity().getZV());
        oneTickVelocity = new Vector(0, 0, 0);

        MineBot.getInstance().getNet().sendPacket(new PacketOutPosition(x, y, z, isOnGround()));
        if(resetY) {
            getVelocity().setYV(+0);
            resetY = false;
        }
    }
}
