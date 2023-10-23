package systems.kinau.fishingbot.gui.config;

import javafx.util.StringConverter;

public class DisplayNameConverter extends StringConverter<DisplayNameProvider> {
    @Override
    public String toString(DisplayNameProvider provider) {
        return provider.getDisplayName();
    }

    @Override
    public DisplayNameProvider fromString(String string) {
        return null;
    }
}
