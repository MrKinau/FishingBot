/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.mining;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.play.PacketOutDig;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosition;

public class Player {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;
    @Getter @Setter private boolean onGround = true;
    @Getter @Setter private boolean resetY = false;
    @Getter private Vector velocity = new Vector(0, 0, 0);
    @Getter private boolean dig = false;

    public void onGroundCheck() {
        World world = MineBot.getInstance().getWorld();
        if (world != null) {
            BlockType bt = world.getBlockAt((int) x, (int) (y + getVelocity().getYV()) - 1, (int) z);
            if (bt.getId() == 0 && isOnGround())
                setOnGround(false);
        }
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
                BlockType bt = world.getBlockAt((int) x, (int) (y + yTest), (int) z);
                if (bt.getId() != 0) {
                    getVelocity().setYV(-(y - (int) y));
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

    public void addMotion(double x, double y, double z) {
        getVelocity().addX(x);
        getVelocity().addY(y);
        getVelocity().addZ(z);
    }

    public void dig(byte digStatus, int x, int y, int z, byte blockFace) {
        MineBot.getInstance().getNet().sendPacket(new PacketOutDig(digStatus, x, y, z, blockFace));
    }

    public void tick() {
        permaMove(0.1, 0, 0);
        if(!dig) {
            dig(DigStatus.STARTED_DIGGING, (int) x, (int) y - 1, (int) z, BlockFace.UP);
            dig(DigStatus.FINISHED_DIGGING, (int) x, (int) y - 1, (int) z, BlockFace.UP);
        }
        dig = true;
        onGroundCheck();
        if(!isOnGround())
            applyGravity();

        x += getVelocity().getXV();
        y += getVelocity().getYV();
        z += getVelocity().getZV();

        World world = MineBot.getInstance().getWorld();
        if (world != null) {
            BlockType bt = world.getBlockAt((int) x, (int) y + 1, (int) z);
            MineBot.getLog().info("onGround: " + isOnGround() + "(" + bt.getId() + ", " + x + "/" + y + "/" + z + "), velocity: " + getVelocity().getXV() + "/" + getVelocity().getYV() + "/" + getVelocity().getZV());
        }
        MineBot.getInstance().getNet().sendPacket(new PacketOutPosition(x, y, z, isOnGround()));
        if(resetY) {
            getVelocity().setYV(+0);
            resetY = false;
        }
    }

}
