/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.mining;

public class DigStatus {

    public static final byte STARTED_DIGGING = 0;
    public static final byte CANCELLED_DIGGING = 1;
    public static final byte FINISHED_DIGGING = 2;
    public static final byte DROP_ITEM_STACK = 3;
    public static final byte DROP_ITEM = 4;
    public static final byte SHOOT_ARROW_FINISH_EATING = 5;
}
