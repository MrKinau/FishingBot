package systems.kinau.fishingbot.modules.fishing;

import lombok.Getter;

@Getter
public class Bobber {

    private final int entityId;
    private final double originX;
    private final double originY;
    private final double originZ;
    private double currentX;
    private double currentY;
    private double currentZ;
    private long creationTime;

    public Bobber(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.currentX = originX;
        this.currentY = originY;
        this.currentZ = originZ;
        this.creationTime = System.currentTimeMillis();
    }

    public boolean existsForAtLeast(long timeInMillis) {
        return creationTime + timeInMillis < System.currentTimeMillis();
    }

    public void move(short dX, short dY, short dZ) {
        double xDiff = dX / 4096.0;
        double yDiff = dY / 4096.0;
        double zDiff = dZ / 4096.0;
        this.currentX += xDiff;
        this.currentY += yDiff;
        this.currentZ += zDiff;
    }

    public void teleport(double x, double y, double z) {
        this.currentX = x;
        this.currentY = y;
        this.currentZ = z;
    }
}
