package systems.kinau.fishingbot.gui;

import com.jthemedetecor.OsThemeDetector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.gui.config.DisplayNameProvider;

@AllArgsConstructor
@Getter
public enum Theme implements DisplayNameProvider {
    SYSTEM("System"),
    DARK("Dark"),
    LIGHT("Light");

    private final String displayName;

    public boolean isDarkMode() {
        if (this == DARK)
            return true;
        else if (this == LIGHT)
            return false;
        else {
            try {
                if (!OsThemeDetector.isSupported()) return false;
                return OsThemeDetector.getDetector().isDark();
            } catch (Throwable ex) {
                FishingBot.getI18n().severe("could-not-detect-theme", ex.getClass().getSimpleName() + ": " + ex.getMessage(), System.getProperty("java.version"));
            }
        }
        return false;
    }
}
