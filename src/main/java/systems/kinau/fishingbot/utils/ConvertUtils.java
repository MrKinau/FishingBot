/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.gui.Theme;
import systems.kinau.fishingbot.i18n.Language;
import systems.kinau.fishingbot.modules.ejection.EjectionRule;
import systems.kinau.fishingbot.modules.fishing.AnnounceType;
import systems.kinau.fishingbot.modules.timer.Timer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConvertUtils {

    private static final Gson GSON = new Gson();

    public static Object fromConfigValue(String value, Class type, Type genericType) {
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
        } else if(type.isAssignableFrom(EjectionRule.EjectionType.class)) {
            try {
                return EjectionRule.EjectionType.valueOf(value);
            } catch (IllegalArgumentException ex) {
                FishingBot.getI18n().warning("config-unknown-announce-type", value);
                ex.printStackTrace();
                return EjectionRule.EjectionType.DROP;
            }
        } else if(type.isAssignableFrom(LocationUtils.Direction.class)) {
            try {
                return LocationUtils.Direction.valueOf(value);
            } catch (IllegalArgumentException ex) {
                FishingBot.getI18n().warning("config-unknown-announce-type", value);
                ex.printStackTrace();
                return LocationUtils.Direction.SOUTH;
            }
        } else if (type.isAssignableFrom(Language.class)) {
            try {
                return Language.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                FishingBot.getLog().severe("Could not find language " + value + ". Falling back to default language ENGLISH");
                return Language.ENGLISH;
            }
        } else if (type.isAssignableFrom(Theme.class)) {
            try {
                return Theme.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                FishingBot.getLog().severe("Could not find theme " + value + ". Falling back to default theme SYSTEM");
                return Theme.SYSTEM;
            }
        } else if (type.isAssignableFrom(List.class) && ((ParameterizedType)genericType).getActualTypeArguments()[0].equals(EjectionRule.class)) {
            try {
                return GSON.<List<EjectionRule>>fromJson(value, new TypeToken<List<EjectionRule>>(){}.getType());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else if (type.isAssignableFrom(List.class) && ((ParameterizedType)genericType).getActualTypeArguments()[0].equals(Timer.class)) {
            try {
                return GSON.<List<Timer>>fromJson(value, new TypeToken<List<Timer>>(){}.getType());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else if (type.isAssignableFrom(List.class)) {
            // should be string list
            try {
                JsonArray array = new JsonParser().parse(value).getAsJsonArray();
                List<String> list = new ArrayList<>();
                array.forEach(jsonElement -> {
                    if (jsonElement.isJsonPrimitive())
                        list.add(jsonElement.getAsString());
                });
                return list;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void toConfigValue(JsonObject object, String key, Object value, Type genericType) {
        if (value instanceof Number) {
            Number number = (Number) value;
            object.addProperty(key, number);
        } else if (value instanceof String) {
            String string = (String) value;
            object.addProperty(key, string);
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            object.addProperty(key, bool);
        } else if (value instanceof Character) {
            Character character = (Character) value;
            object.addProperty(key, character);
        } else if (value instanceof JsonElement) {
            JsonElement jsonElement = (JsonElement) value;
            object.add(key, jsonElement);
        } else {
            object.add(key, GSON.toJsonTree(value));
        }
    }
}
