/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/12
 */

package systems.kinau.fishingbot.mining;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.block.BlockChangeEvent;

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
        System.out.println(angle);
        if (angle >= 45 && angle < 135)
            return BlockFace.X_NEGATIVE;
        else if (angle >= 135 && angle < 225)
            return BlockFace.Z_NEGATIVE;
        else if (angle >= 225 && angle < 315)
            return BlockFace.X_POSITIVE;
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
        while (currBlock == null) {
            Player player = MineBot.getInstance().getPlayer();
            World world = MineBot.getInstance().getWorld();

            int x = Double.valueOf(Math.floor(player.getX())).intValue();
            int y = Double.valueOf(Math.floor(player.getY())).intValue();
            int z = Double.valueOf(Math.floor(player.getZ())).intValue();
            BlockType typeDown = world.getBlockAt(x, y, z, currentDirection);
            BlockType typeUp = world.getBlockAt(x, y + 1, z, currentDirection);
            BlockType typeUpper = world.getBlockAt(x, y + 2, z, currentDirection);

            BlockType curr = typeDown;
            if (curr.getId() == 0) {
                curr = typeUp;
                y++;
            }

            if (curr.getId() != 0) {
                MineBot.getLog().info("down: " + typeDown.getId());
                MineBot.getLog().info("up: " + typeUp.getId());
                MineBot.getLog().info("up + 1: " + typeUpper.getId());
                switch (currentDirection) {
                    case BlockFace.X_NEGATIVE: x--;break;
                    case BlockFace.X_POSITIVE: x++;break;
                    case BlockFace.Z_NEGATIVE: z--;break;
                    case BlockFace.Z_POSITIVE: z++;break;
                    case BlockFace.UP: y++;break;
                    case BlockFace.DOWN: y--;break;
                }
                if(curr.getId() == 12 || curr.getId() == 13)
                    careful = true;
                else if (typeUp.getId() == 12 || typeUp.getId() == 13)
                    careful = true;
                else if (typeUpper.getId() == 12 || typeUpper.getId() == 13)
                    careful = true;
                if(careful)
                    MineBot.getLog().info("careful ---");
                MineBot.getLog().info("+++++++++++++++++++++++++++++++++++++++++++");
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

    @EventHandler
    public void onBlockUpdate(BlockChangeEvent event) {
        if(currBlock == null)
            return;
        if(event.getX() == currBlock.getX() && event.getY() == currBlock.getY() && event.getZ() == currBlock.getZ()) {
            currBlock = null;
            if(!careful)
                new Thread(this::mineNextBlock).start();
            new Thread(() -> {
                if(careful) {
                    MineBot.getLog().info("WAAAAAIIIIIIT");
                    try {
                        Thread.sleep(1300);
                        careful = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new Thread(this::mineNextBlock).start();
                }
                if(!MineBot.getInstance().getPlayer().testWallCollision(currentDirection)) {
                    move();
                }
            }).start();
        }
    }


}
