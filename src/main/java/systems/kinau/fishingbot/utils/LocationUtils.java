package systems.kinau.fishingbot.utils;

import lombok.Getter;

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

    public enum Direction {
        NORTH(180.0F),
        EAST(-90.0F),
        SOUTH(0.0F),
        WEST(90.0F),
        DOWN(Float.MIN_VALUE, 90.0F);

        @Getter private float yaw = Float.MIN_VALUE;
        @Getter private float pitch = Float.MIN_VALUE;

        Direction(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        Direction(float yaw) {
            this.yaw = yaw;
        }
    }

}