package systems.kinau.fishingbot.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.gui.config.DisplayNameProvider;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created: 13.10.2020
 *
 * @author Summerfeeling
 */
@AllArgsConstructor
@Getter
public enum Language implements DisplayNameProvider {

    CHINESE_SIMPLIFIED(new Locale("zh", "CN"), "Chinese Simplified"),
    CHINESE_TRADITIONAL(new Locale("zh", "TW"), "Chinese Traditional"),
    ENGLISH(new Locale("en", "EN"), "English"),
    FRENCH(new Locale("fr", "FR"), "French"),
    GERMAN(new Locale("de", "DE"), "German"),
    ITALIAN(new Locale("it", "IT"), "Italian"),
    POLISH(new Locale("pl", "PL"), "Polish"),
    RUSSIAN(new Locale("ru", "RU"), "Russian"),
    SPANISH(new Locale("es", "ES"), "Spanish");

    private final Locale locale;
    private final String displayName;

    public String getLanguageCode() {
        return locale.toLanguageTag().replace("-", "_");
    }

    public static Language getByLocale(Locale locale) {
        return Arrays.stream(values())
                .filter(language -> language.locale.equals(locale))
                .findAny()
                .orElse(Language.ENGLISH);
    }

}
