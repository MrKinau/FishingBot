package systems.kinau.fishingbot.utils;

import lombok.Getter;
import systems.kinau.fishingbot.gui.config.DisplayNameProvider;

public class LocationUtils {

    public static float yawDiff(float yaw1, float yaw2) {
        if (yaw1 == yaw2)
            return 0;
        float maxX = Math.max(yaw1, yaw2);
        float minX = Math.min(yaw1, yaw2);
        float xDiff = Math.abs(maxX - minX);
        if (yaw1 < 0 && yaw2 > 0 && xDiff > 180) {
            float r = 180 + yaw1;
            return -r - (180 - yaw2);
        }
        if (yaw1 > 0 && yaw2 < 0 && xDiff > 180) {
            float r = -180 + yaw1;
            return -r + (180 + yaw2);
        }
        if (yaw1 > yaw2) {
            return -xDiff;
        } else {
            return xDiff;
        }
    }

    public static long toBlockPos(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    public static float normalizeYaw(float yaw) {
        yaw = yaw % 360f;
        if (yaw < -180f) yaw += 360f;
        else if (yaw > 180f) yaw -= 360f;
        return yaw;
    }

    public static float normalizePitch(float pitch) {
        pitch = pitch % 180f;
        if (pitch < -90f) pitch += 180f;
        else if (pitch > 90f) pitch -= 180f;
        return pitch;
    }

    @Getter
    public enum Direction implements DisplayNameProvider {
        NORTH(180.0F, "North"),
        EAST(-90.0F, "East"),
        SOUTH(0.0F, "South"),
        WEST(90.0F, "West"),
        DOWN(Float.MIN_VALUE, 90.0F, "Down");

        private float yaw = Float.MIN_VALUE;
        private float pitch = Float.MIN_VALUE;
        private String displayName;

        Direction(float yaw, float pitch, String displayName) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.displayName = displayName;
        }

        Direction(float yaw, String displayName) {
            this.yaw = yaw;
            this.displayName = displayName;
        }
    }

}