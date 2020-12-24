/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io.config;

import org.json.simple.parser.ParseException;
import systems.kinau.fishingbot.FishingBot;

import java.io.IOException;

public interface Config {

    default void init(String dir) {
        try {
            new PropertyProcessor().processAnnotations(this, dir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            FishingBot.getI18n().severe("config-failed-parsing", e.toString());
        }
    }

}

