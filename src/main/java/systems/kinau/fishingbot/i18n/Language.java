package systems.kinau.fishingbot.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created: 13.10.2020
 *
 * @author Summerfeeling
 */
@AllArgsConstructor
@Getter
public enum Language {

    ENGLISH(new Locale("en", "EN")),
    FRENCH(new Locale("fr", "FR")),
    GERMAN(new Locale("de", "DE")),
    POLISH(new Locale("pl", "PL")),
    RUSSIAN(new Locale("ru", "RU")),
    SPANISH(new Locale("es", "ES")),
    ITALIAN(new Locale("it", "IT"));

    private final Locale locale;

    public String getLanguageCode() {
        return locale.getLanguage();
    }

    public static Language getByLocale(Locale locale) {
        return Arrays.stream(values())
                .filter(language -> language.locale.equals(locale))
                .findAny()
                .orElse(Language.ENGLISH);
    }

}
