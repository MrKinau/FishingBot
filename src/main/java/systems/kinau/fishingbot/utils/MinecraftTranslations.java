package systems.kinau.fishingbot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;

public class MinecraftTranslations {

    private final JsonObject translations;

    public MinecraftTranslations() {
        JsonParser parser = new JsonParser();
        String file = "mc_en_us.json";
        JsonObject translations = null;
        try {
            translations = parser.parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file))).getAsJsonObject();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        this.translations = translations;
    }

    public String getTranslation(String key, Object... args) {
        if (!translations.has(key))
            return null;
        return String.format(translations.get(key).getAsString(), args);
    }

    public String getEnchantmentName(String id) {
        id = id.replace("minecraft:", "");
        String translation = getTranslation("enchantment.minecraft." + id);
        if (translation == null)
            return id;
        return translation;
    }

    public String getItemName(String id) {
        id = id.replace("minecraft:", "");
        String translation = getTranslation("item.minecraft." + id);
        if (translation == null) {
            translation = getTranslation("block.minecraft." + id);
            if (translation == null)
                return id;
        }
        return translation;
    }
}
