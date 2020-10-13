package systems.kinau.fishingbot.i18n;

import systems.kinau.fishingbot.FishingBot;

import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created: 13.10.2020
 *
 * @author Summerfeeling
 */
public class I18n {

    private Map<String, String> locales = new HashMap<>();
    private String prefix = "FishingBot";

    public I18n(Language language, String defaultPrefix) {
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(I18n.class.getResourceAsStream("/lang/fb_" + language.getLanguageCode() + ".properties")));
            this.locales = new HashMap(properties);

            FishingBot.getLog().info("Using language: " + language + " @ /lang/" + language.getLanguageCode() + ".properties");
            this.prefix = locales.getOrDefault("prefix", defaultPrefix);
        } catch (Exception e) {
            FishingBot.getLog().severe("Failed loading language " + language.name() + ": /lang" + language.getLanguageCode() + ".properties");
            FishingBot.getLog().severe("Falling back to default langauge ENGLISH");
            e.printStackTrace();
        }
    }

    public String t(String key, Object... args) {
        String value = locales.get(key);
        return value == null ? "N/A" : MessageFormat.format(value, args).replace("%prefix%", prefix);
    }

    public String getPrefix() {
        return prefix;
    }
}
