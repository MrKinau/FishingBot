/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io;

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
            FishingBot.getLog().severe("*****************************************************************************");
            FishingBot.getLog().severe("Your config could not be parsed, because it does not fit the JSON-Style:");
            FishingBot.getLog().severe(e.toString());
            FishingBot.getLog().severe("*****************************************************************************");
        }
    }

}

