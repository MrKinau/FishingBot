/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthService;
import systems.kinau.fishingbot.i18n.Language;
import systems.kinau.fishingbot.modules.ejection.EjectionRule;
import systems.kinau.fishingbot.modules.fishing.AnnounceType;
import systems.kinau.fishingbot.modules.timer.Timer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConvertUtils {

    public static Object convert(String value, Class type, Type genericType) {
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
                FishingBot.getLog().severe("Could not find language " + value + ". Falling back to default langugae ENGLISH");
                return Language.ENGLISH;
            }
        } else if (type.isAssignableFrom(AuthService.class)) {
            try {
                return AuthService.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                FishingBot.getI18n().warning("config-unknown-auth-service", value, Arrays.stream(AuthService.values()).map(Enum::name).collect(Collectors.joining(", ")));
                ex.printStackTrace();
                return LocationUtils.Direction.SOUTH;
            }
        } else if (type.isAssignableFrom(List.class) && ((ParameterizedType)genericType).getActualTypeArguments()[0].equals(EjectionRule.class)) {
            try {
                List<EjectionRule> rules = new ArrayList<>();
                JSONArray array = (JSONArray) new JSONParser().parse(value);
                array.forEach(o -> {
                    try {
                        JSONObject obj = (JSONObject) new JSONParser().parse(o.toString());
                        String name = obj.get("name").toString();
                        LocationUtils.Direction direction = LocationUtils.Direction.valueOf((String) obj.get("direction"));
                        EjectionRule.EjectionType ejectionType = EjectionRule.EjectionType.valueOf((String) obj.get("ejectionType"));
                        JSONArray allowList = (JSONArray) new JSONParser().parse(obj.get("allowList").toString());
                        rules.add(new EjectionRule(name, direction, new ArrayList<String>(allowList), ejectionType));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return rules;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else if (type.isAssignableFrom(List.class) && ((ParameterizedType)genericType).getActualTypeArguments()[0].equals(Timer.class)) {
            try {
                List<Timer> timers = new ArrayList<>();
                JSONArray array = (JSONArray) new JSONParser().parse(value);
                array.forEach(o -> {
                    try {
                        JSONObject obj = (JSONObject) new JSONParser().parse(o.toString());
                        String name = obj.get("name").toString();
                        int units = Integer.valueOf(obj.get("units").toString());
                        TimeUnit timeUnit = TimeUnit.valueOf((String) obj.get("timeUnit"));
                        JSONArray command = (JSONArray) new JSONParser().parse(obj.get("commands").toString());
                        timers.add(new Timer(name, units, timeUnit, new ArrayList<String>(command)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return timers;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
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
