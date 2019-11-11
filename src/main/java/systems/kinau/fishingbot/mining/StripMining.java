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
import systems.kinau.fishingbot.network.utils.MaterialMc18;

public class StripMining implements Listener {

    @Getter private byte currentDirection;
    @Getter private Position currBlock;
    @Getter private boolean careful;

    public StripMining() {
        MineBot.getInstance().getEventManager().registerListener(this);
        this.currentDirection = getStartDirection();
        fixDirection();
        start();
    }

    private byte getStartDirection() {
        float yaw = MineBot.getInstance().getPlayer().getYaw();
        float angle = Math.abs(yaw % 360);
        if (angle >= 45 && angle < 135)
            return BlockFace.X_POSITIVE;
        else if (angle >= 135 && angle < 225)
            return BlockFace.Z_NEGATIVE;
        else if (angle >= 225 && angle < 315)
            return BlockFace.X_NEGATIVE;
        else
            return BlockFace.Z_POSITIVE;
    }

    private void fixDirection() {
        Player player = MineBot.getInstance().getPlayer();
        switch (currentDirection) {
            case BlockFace.X_POSITIVE: player.setYaw(-90); break;
            case BlockFace.Z_NEGATIVE: player.setYaw(180); break;
            case BlockFace.X_NEGATIVE: player.setYaw(90); break;
            case BlockFace.Z_POSITIVE: player.setYaw(0); break;
        }
        player.addOneTickMotion((Math.floor(player.getX()) + 0.5) - player.getX(), 0, (Math.floor(player.getZ()) + 0.5) - player.getZ());
    }

    private void start() {
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
        if(currBlock == null)
            return;
        if(event.getX() == currBlock.getX() && event.getY() == currBlock.getY() && event.getZ() == currBlock.getZ() && event.getBlock().getMaterial() == MaterialMc18.AIR) {
            new Thread(() -> {
                byte interruptingFluid = isFluidInterrupting();
                while (interruptingFluid != BlockFace.NONE) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MineBot.getLog().warning("Interrupting Fluid at: " + isFluidInterrupting() + " " + currBlock.getY());
                    World world = MineBot.getInstance().getWorld();
                    Position fluidPos = world.getRelativePosition(currBlock.getX(), currBlock.getY(), currBlock.getZ(), interruptingFluid);
                    Position blockClickPos = world.getAdjacentBlock(fluidPos);
                    System.out.println("FLUID BLOCK POS: " + fluidPos.getX() + " " + fluidPos.getY() + " " + fluidPos.getZ());
                    if (blockClickPos == null) {
                        //TODO: Change this behaviour
                        MineBot.getLog().severe("PENIS STUCK IN TOASTER! QUIT!");
                        System.exit(1);
                    }
                    System.out.println("CLICK BLOCK POS: " + blockClickPos.getX() + " " + blockClickPos.getY() + " " + blockClickPos.getZ() + ", " + world.getAdjacentDirection(fluidPos, blockClickPos));
                    MineBot.getInstance().getPlayer().place(1, blockClickPos.getX(), blockClickPos.getY(), blockClickPos.getZ(), world.getAdjacentDirection(fluidPos, blockClickPos));
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    interruptingFluid = isFluidInterrupting();
                }
                currBlock = null;
                if(!careful) {
                    new Thread(this::mineNextBlock).start();
                    if (!MineBot.getInstance().getPlayer().testWallCollision(currentDirection)) {
                        move();
                    }
                } else {
                    new Thread(() -> {
                        if (careful) {
                            try {
                                Thread.sleep(1300);
                                careful = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            new Thread(this::mineNextBlock).start();
                        }
                    }).start();
                }
            }).start();
        }
    }


}
