/*
 * Created by David Luedtke (MrKinau)
 * 2019/11/2
 */

package systems.kinau.fishingbot.network.utils;

public class InvalidPacketException extends RuntimeException {

    public InvalidPacketException(String msg) {
        super(msg);
    }
}
