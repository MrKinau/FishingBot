/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.i18n.Language;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtils {

    public static Object convert(String value, Class type) {
        if(type.isAssignableFrom(String.class)) {
            return value;
        } else if(type.isAssignableFrom(double.class)) {
            return Double.valueOf(value);
        } else if(type.isAssignableFrom(boolean.class)) {
            return Boolean.valueOf(value);
        } else if(type.isAssignableFrom(byte.class)) {
            return Byte.valueOf(value);
        } else if(type.isAssignableFrom(float.class)) {
            return Float.valueOf(value);
        } else if(type.isAssignableFrom(int.class)) {
            return Integer.valueOf(value);
        } else if(type.isAssignableFrom(long.class)) {
            return Long.valueOf(value);
        } else if(type.isAssignableFrom(AnnounceType.class)) {
            try {
                return AnnounceType.valueOf(value);
            } catch (IllegalArgumentException ex) {
                FishingBot.getI18n().warning("config-unknown-announce-type", value);
                ex.printStackTrace();
                return AnnounceType.ALL;
            }
        } else if (type.isAssignableFrom(Language.class)) {
            try {
                return Language.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                FishingBot.getLog().severe("Could not find language " + value + ". Falling back to default langugae ENGLISH");
                return Language.ENGLISH;
            }
        } else if (type.isAssignableFrom(List.class)) {
            // should be string list
            try {
                JSONArray array = (JSONArray) new JSONParser().parse(value);
                return new ArrayList<>(array);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
