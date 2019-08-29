/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface Config {

    default void init(String dir, String comments) {
        try {
            new PropertyProcessor().processAnnotations(this, dir, comments);
        } catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

