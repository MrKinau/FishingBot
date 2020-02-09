/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/12
 */

package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.block.BlockChangeEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutHeldItemChange;
import systems.kinau.fishingbot.network.utils.MaterialMc18;

public class StripMining implements Listener {

    private final int BIG_LENGTH = 30;
    private final int SMALL_LENGTH = 3;

    @Getter private byte currentDirection;
    @Getter private Position currBlock;
    @Getter private boolean careful;
    @Getter private boolean curve;
    @Getter private boolean left;

    @Getter private Position startPos;

    @Getter private int stepsLeft = BIG_LENGTH;
    @Getter private boolean paused;

    public StripMining() {
        MineBot.getInstance().getEventManager().registerListener(this);
        this.currentDirection = MineBot.getInstance().getPlayer().getDirection();
        start();
    }

    private void start() {
        this.startPos = MineBot.getInstance().getPlayer().getPosition();
        MineBot.getInstance().getNet().sendPacket(new PacketOutHeldItemChange((short)0));
        move();
        new Thread(() -> {
            while (currBlock == null) {
                mineNextBlock();
            }
        }).start();
    }

    private void move() {
        Player player = MineBot.getInstance().getPlayer();
        switch (currentDirection) {
            case BlockFace.X_POSITIVE: player.permaMove(0.15, 0, 0); break;
            case BlockFace.Z_NEGATIVE: player.permaMove(0, 0, -0.15); break;
            case BlockFace.X_NEGATIVE: player.permaMove(-0.15, 0, 0); break;
            case BlockFace.Z_POSITIVE: player.permaMove(0, 0, 0.15); break;
        }
    }

    private void mineNextBlock() {
        Player player = MineBot.getInstance().getPlayer();
        World world = MineBot.getInstance().getWorld();
        while (currBlock == null) {
            if (isPaused())
                break;
            int x = Double.valueOf(Math.floor(player.getX())).intValue();
            int y = Double.valueOf(Math.floor(player.getY())).intValue();
            int z = Double.valueOf(Math.floor(player.getZ())).intValue();
            BlockType typeDown = world.getBlockAt(x, y, z, currentDirection);
            BlockType typeUp = world.getBlockAt(x, y + 1, z, currentDirection);
            BlockType typeUpper = world.getBlockAt(x, y + 2, z, currentDirection);

            BlockType curr = typeDown;
            if (!curr.getMaterial().isSolid()) {
                curr = typeUp;
                y++;
            }

            if (curr.getMaterial().isSolid()) {
                Position pos = world.getRelativePosition(x, y, z, currentDirection);

                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();
                if(curr.getMaterial().hasGravity())
                    careful = true;
                else if (typeUp.getMaterial().hasGravity())
                    careful = true;
                else if (typeUpper.getMaterial().hasGravity())
                    careful = true;
                player.dig(DigStatus.STARTED_DIGGING, x, y, z, currentDirection);
                player.dig(DigStatus.FINISHED_DIGGING, x, y, z, currentDirection);
                currBlock = new Position(x, y, z);
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private byte isFluidInterrupting() {
        if(currBlock == null)
            return BlockFace.NONE;
        MaterialMc18 selfMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX(), currBlock.getY(), currBlock.getZ()).getMaterial();
        MaterialMc18 topMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX(), currBlock.getY() + 1, currBlock.getZ()).getMaterial();
        MaterialMc18 bottomMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX(), currBlock.getY() - 1, currBlock.getZ()).getMaterial();
        MaterialMc18 negXMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX() - 1, currBlock.getY(), currBlock.getZ()).getMaterial();
        MaterialMc18 posXMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX() + 1, currBlock.getY(), currBlock.getZ()).getMaterial();
        MaterialMc18 negZMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX(), currBlock.getY(), currBlock.getZ() - 1).getMaterial();
        MaterialMc18 posZMat = MineBot.getInstance().getWorld().getBlockAt(currBlock.getX(), currBlock.getY(), currBlock.getZ() + 1).getMaterial();
        if (negXMat.isFluid())
            return BlockFace.X_NEGATIVE;
        else if (posXMat.isFluid())
            return BlockFace.X_POSITIVE;
        else if (negZMat.isFluid())
            return BlockFace.Z_NEGATIVE;
        else if (posZMat.isFluid())
            return BlockFace.Z_POSITIVE;
        else if (topMat.isFluid())
            return BlockFace.UP;
        else if (bottomMat.isFluid())
            return BlockFace.DOWN;
        else if (selfMat.isFluid())
            return BlockFace.SELF;
        else return BlockFace.NONE;
    }

    @EventHandler
    public void onBlockUpdate(BlockChangeEvent event) {
        if (currBlock == null)
            return;
        if(event.getX() == currBlock.getX() && event.getY() == currBlock.getY() && event.getZ() == currBlock.getZ() && event.getBlock().getMaterial() == MaterialMc18.AIR) {
            new Thread(() -> {
                currBlock = null;
                if (careful) {
                    try {
                        Thread.sleep(1300);
                        careful = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                new Thread(this::mineNextBlock).start();
                if (!MineBot.getInstance().getPlayer().testWallCollision(currentDirection)) {
                    stepsLeft--;
                    if (getStepsLeft() > 0)
                        move();
                    else {
                        paused = true;
                        move();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MineBot.getInstance().getPlayer().turn(aBoolean -> {
                            paused = false;
                            new Thread(this::mineNextBlock).start();
                            if(curve)
                                left = !left;
                            currentDirection = MineBot.getInstance().getPlayer().getDirection();
                            stepsLeft = curve ? BIG_LENGTH : SMALL_LENGTH;
                            curve = !curve;
                            move();
                        }, left);
                    }
                }
            }).start();
        }
    }


}
