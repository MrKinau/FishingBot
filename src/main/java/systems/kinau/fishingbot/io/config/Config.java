/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io.config;

import com.google.gson.JsonParseException;
import systems.kinau.fishingbot.FishingBot;

import java.io.IOException;

public interface Config {

    default void init(String dir) {
        try {
            new PropertyProcessor().processAnnotations(this, dir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            FishingBot.getI18n().severe("config-failed-parsing", e.toString());
        }
    }

}

