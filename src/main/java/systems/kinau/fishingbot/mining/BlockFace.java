/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.mining;

public class BlockFace {

    public static final byte NONE = -1;
    public static final byte DOWN = 0;
    public static final byte UP = 1;
    public static final byte Z_NEGATIVE = 2;
    public static final byte Z_POSITIVE = 3;
    public static final byte X_NEGATIVE = 4;
    public static final byte X_POSITIVE = 5;
    public static final byte SELF = 6;

    public static byte getCorresponding(byte blockFace) {
        switch (blockFace) {
            case DOWN: return UP;
            case UP: return DOWN;
            case X_NEGATIVE: return X_POSITIVE;
            case X_POSITIVE: return X_NEGATIVE;
            case Z_NEGATIVE: return Z_POSITIVE;
            case Z_POSITIVE: return Z_NEGATIVE;
            default: return NONE;
        }
    }

}
