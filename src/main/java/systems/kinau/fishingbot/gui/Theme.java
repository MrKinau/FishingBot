package systems.kinau.fishingbot.gui;

import com.jthemedetecor.OsThemeDetector;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
            if (!OsThemeDetector.isSupported()) return false;
            try {
                return OsThemeDetector.getDetector().isDark();
            } catch (Throwable ignore) {}
        }
        return false;
    }
}
