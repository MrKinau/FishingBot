package systems.kinau.fishingbot.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    GERMAN(new Locale("de", "DE"));

    private Locale locale;

    public String getLanguageCode() {
        return locale.getLanguage();
    }

}
