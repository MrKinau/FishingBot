package systems.kinau.fishingbot.bot;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.ProtocolState;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosLook;
import systems.kinau.fishingbot.utils.LocationUtils;

import java.util.function.Consumer;

/**
 * Controller class responsible for managing player look/rotation logic.
 * Extracted from Player class to improve separation of concerns.
 */
public class LookController {

    private final Player player;
    @Getter private Thread lookThread;

    public LookController(Player player) {
        this.player = player;
    }

    /**
     * Look in a specific direction with a callback when finished
     *
     * @param direction The direction to look
     * @param onFinish Callback when look is complete
     * @return true if look started successfully, false if already looking
     */
    public boolean look(LocationUtils.Direction direction, Consumer<Boolean> onFinish) {
        float yaw = direction.getYaw() == Float.MIN_VALUE ? player.getYaw() : direction.getYaw();
        float pitch = direction.getPitch() == Float.MIN_VALUE ? player.getPitch() : direction.getPitch();
        return look(yaw, pitch, FishingBot.getInstance().getCurrentBot().getConfig().getLookSpeed(), onFinish);
    }

    /**
     * Look at specific yaw and pitch with custom speed
     *
     * @param yaw Target yaw
     * @param pitch Target pitch
     * @param speed Look speed
     * @return true if look started successfully
     */
    public boolean look(float yaw, float pitch, int speed) {
        return look(yaw, pitch, speed, null);
    }

    /**
     * Look at specific yaw and pitch with custom speed and callback
     *
     * @param yaw Target yaw
     * @param pitch Target pitch
     * @param speed Look speed (degrees per step)
     * @param onFinish Callback when look is complete
     * @return true if look started successfully, false if already looking
     */
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
        lookThread.start();
        return true;
    }

    /**
     * Internal method that performs the actual looking animation
     *
     * @param yaw Target yaw
     * @param pitch Target pitch
     * @param speed Look speed
     * @param onFinish Callback when complete
     */
    private void internalLook(float yaw, float pitch, int speed, Consumer<Boolean> onFinish) {
        float yawDiff = LocationUtils.yawDiff(player.getYaw(), yaw);
        float pitchDiff = LocationUtils.yawDiff(player.getPitch(), pitch);

        int steps = (int) Math.ceil(Math.max(Math.abs(yawDiff), Math.abs(pitchDiff)) / Math.max(1, speed));
        float yawPerStep = yawDiff / steps;
        float pitchPerStep = pitchDiff / steps;

        for (int i = 0; i < steps; i++) {
            player.setYaw(LocationUtils.normalizeYaw(player.getYaw() + yawPerStep));
            player.setPitch(LocationUtils.normalizePitch(player.getPitch() + pitchPerStep));
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                new PacketOutPosLook(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), true, true)
            );
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
                return;
            }
        }
        
        if (onFinish != null && FishingBot.getInstance().getCurrentBot().getNet().getState() == ProtocolState.PLAY)
            onFinish.accept(true);

        try {
            Thread.sleep(50);
        } catch (InterruptedException ignore) { }
    }

    /**
     * Check if currently performing a look animation
     *
     * @return true if currently looking
     */
    public boolean isCurrentlyLooking() {
        return !(lookThread == null || lookThread.isInterrupted() || !lookThread.isAlive());
    }

    /**
     * Interrupt the current look thread if one is running
     */
    public void interruptLook() {
        if (lookThread != null) {
            lookThread.interrupt();
            this.lookThread = null;
        }
    }
}
