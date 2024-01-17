/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io.config;

import com.google.gson.*;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.utils.ConvertUtils;
import systems.kinau.fishingbot.utils.ReflectionUtils;

import java.io.*;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.*;

public class PropertyProcessor {

    private static final Gson PRETTY_GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public void processAnnotations(final Config config, String dir) throws AnnotationFormatError, IOException, JsonParseException {
        File file = new File(dir);
        File parentDir = file.getParentFile();
        File probOldConfig = new File(parentDir, "config.properties");

        if (probOldConfig.exists()) {
            transformFromOldConfig(config, probOldConfig, file);
            return;
        }

        if (!file.exists()) {
            if (parentDir != null && !parentDir.exists())
                parentDir.mkdirs();
            saveConfig(config, file);
            return;
        }

        JsonElement configJsonElement = new JsonParser().parse(new FileReader(file));
        if (configJsonElement == null || !configJsonElement.isJsonObject())
            throw new JsonParseException("Parsed Config Json is not a JSON object, got " + configJsonElement);
        JsonObject configJson = configJsonElement.getAsJsonObject();
        boolean unsetConfigOptions = false;

        List<Field> fields = ReflectionUtils.getAllFields(config);
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Property.class))
                continue;
            Property propAnnotation = field.getAnnotation(Property.class);

            String key = propAnnotation.key().trim();
            String source = dir.trim();

            if (key.isEmpty() || source.isEmpty())
                throw new AnnotationFormatError("Property Annotation needs source and key");

            JsonElement value = getValueByDottedKey(configJson, key);
            if (value != null) {
                if (!convertChangedFields(key, value, field, config, configJson)) {
                    Object typedValue = ConvertUtils.fromConfigValue(jsonToString(value), field.getType(), field.getGenericType());
                    if (typedValue == null)
                        throw new ConvertException("Cannot convert type from " + field.getName() + ":" + field.getType().getSimpleName());
                    ReflectionUtils.setField(field, config, typedValue);
                } else
                    unsetConfigOptions = true;
            } else
                unsetConfigOptions = true;
        }

        // fix config (add undefined keys)
        if (unsetConfigOptions) {
            FishingBot.getI18n().warning("config-missing-fields-detected");
            saveConfig(config, file);
        }
    }

    private String jsonToString(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
            return element.getAsString();
        return element.toString();
    }

    private boolean convertChangedFields(String key, JsonElement value, Field field, Config config, JsonObject configJson) {
        if (key.equals("start-text.text") && !value.toString().startsWith("[")) {
            FishingBot.getI18n().info("config-converting");
            ReflectionUtils.setField(field, config, Arrays.asList(value.getAsString().split(";")));
            try {
                Field discordField1 = config.getClass().getDeclaredField("webHookEnabled");
                Field discordField2 = config.getClass().getDeclaredField("webHook");

                Object value1 = getValueByDottedKey(configJson, "discord.enabled");
                if (value1 != null) {
                    value1 = ConvertUtils.fromConfigValue(jsonToString((JsonElement) value1), boolean.class, boolean.class);
                    ReflectionUtils.setField(discordField1, config, value1);
                }

                Object value2 = getValueByDottedKey(configJson, "discord.web-hook");
                if (value2 != null) {
                    value2 = ConvertUtils.fromConfigValue(jsonToString((JsonElement) value2), String.class, String.class);
                    ReflectionUtils.setField(discordField2, config, value2);
                }
            } catch (NoSuchFieldException ignore) { }
            return true;
        }
        return false;
    }

    private JsonElement getValueByDottedKey(JsonObject object, String key) {
        String[] parts = key.split("\\.");
        JsonElement current = object;
        for (String part : parts) {
            if (current != null && current.isJsonObject()) {
                if (current.getAsJsonObject().has(part)) {
                    current = current.getAsJsonObject().get(part);
                } else
                    return null;
            } else
                return null;
        }
        return current;
    }

    public void saveConfig(Config config, File file) {
        Map<String, Object> configOptions = new HashMap<>();
        List<Field> fields = ReflectionUtils.getAllFields(config);
        JsonObject root = new JsonObject();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Property.class))
                continue;
            Property propAnnotation = field.getAnnotation(Property.class);

            String key = propAnnotation.key().trim();
            Object value = ReflectionUtils.getField(field, config);
            configOptions.put(key, value);

            if (!key.contains(".")) {
                ConvertUtils.toConfigValue(root, key, configOptions.get(key), field.getGenericType());
                return;
            }
            String[] parts = key.split("\\.");
            JsonObject curr = root;
            for (String part : Arrays.copyOfRange(parts, 0, parts.length - 1)) {
                if (!curr.has(part))
                    curr.add(part, curr = new JsonObject());
                else
                    curr = curr.get(part).getAsJsonObject();
            }
            ConvertUtils.toConfigValue(curr, parts[parts.length - 1], configOptions.get(key), field.getGenericType());
        }

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(PRETTY_GSON.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transformFromOldConfig(Config config, File oldConfig, File newConfig) throws IOException {
        Properties properties = new Properties();
        FileInputStream fos;
        properties.load(fos = new FileInputStream(oldConfig));
        List<Field> fields = ReflectionUtils.getAllFields(config);
        for (Field field : fields) {
            if(!field.isAnnotationPresent(Property.class))
                continue;
            Property propAnnotation = field.getAnnotation(Property.class);

            String key = translateKey(propAnnotation.key().trim());
            if (key == null)
                continue;

            String value = properties.getProperty(key);

            Object typedValue = ConvertUtils.fromConfigValue(value, field.getType(), field.getGenericType());
            if (typedValue == null)
                throw new ConvertException("Cannot convert type from " + field.getName() + ":" + field.getType().getSimpleName());
            ReflectionUtils.setField(field, config, typedValue);
        }
        saveConfig(config, newConfig);
        fos.close();
        if (!oldConfig.delete())
            oldConfig.deleteOnExit();

        FishingBot.getI18n().info("config-converted-properties-to-json");
    }

    private String translateKey(String newKey) {
        switch (newKey) {
            case "server.ip": return "server-ip";
            case "server.port": return "server-port";
            case "server.realm-id": return "realm-id";
            case "server.realm-accept-tos": return "realm-accept-tos";
            case "auto.auto-reconnect": return "auto-reconnect";
            case "auto.auto-reconnect-time": return "auto-reconnect-time";
            case "server.online-mode": return "online-mode";
            case "account.mail": return "account-username";
            case "account.password": return "account-password";
            case "logs.log-count": return "log-count";
            case "logs.log-packets": return "log-packets";
            case "announces.announce-type-chat": return "announce-type-chat";
            case "announces.announce-type-console": return "announce-type-console";
            case "announces.announce-lvl-up": return "announce-lvl-up";
            case "start-text.enabled": return "start-text-enabled";
            case "start-text.text": return "start-text";
            case "server.default-protocol": return "default-protocol";
            case "discord.web-hook": return "discord-webHook";
            case "auto.auto-disconnect": return "auto-disconnect";
            case "auto.auto-disconnect-players-threshold": return "auto-disconnect-players-threshold";
            case "misc.stucking-fix-enabled": return "stucking-fix-enabled";
            default: return null;
        }
    }
}
