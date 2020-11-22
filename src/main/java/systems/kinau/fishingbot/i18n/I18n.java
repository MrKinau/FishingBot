package systems.kinau.fishingbot.i18n;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created: 13.10.2020
 *
 * @author Summerfeeling
 */
public class I18n {

    private Map<String, String> locales = new HashMap<>();
    private final String prefix;
    @Getter private ResourceBundle bundle;

    public I18n(Language language, String defaultPrefix) {
        this(language, defaultPrefix, false);
    }

    public I18n(Language language, String defaultPrefix, boolean silent) {
        this.prefix = defaultPrefix;
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(I18n.class.getResourceAsStream("/lang/fb_" + language.getLanguageCode() + ".properties"), StandardCharsets.UTF_8));
            this.locales = new HashMap(properties);
            this.bundle = ResourceBundle.getBundle("lang/fb", language.getLocale(), new UTF8Control());

            if (!silent)
                FishingBot.getLog().info("Using language: " + language + " @ /lang/fb_" + language.getLanguageCode() + ".properties");
        } catch (Exception e) {
            FishingBot.getLog().severe("Failed loading language " + language.name() + ": /lang/fb_" + language.getLanguageCode() + ".properties");
            FishingBot.getLog().severe("Falling back to default langauge ENGLISH");
            e.printStackTrace();
        }
    }

    public String t(String key, Object... args) {
        String value = locales.get(key);
        return value == null ? "N/A" : MessageFormat.format(value, args).replace("%prefix%", prefix);
    }

    public void info(String key, Object... args) {
        for (String line : t(key, args).split("\n")) {
            FishingBot.getLog().info(line);
        }
    }

    public void warning(String key, Object... args) {
        for (String line : t(key, args).split("\n")) {
            FishingBot.getLog().warning(line);
        }
    }

    public void severe(String key, Object... args) {
        for (String line : t(key, args).split("\n")) {
            FishingBot.getLog().severe(line);
        }
    }
}
