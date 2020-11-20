package systems.kinau.fishingbot.network.utils;

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

}