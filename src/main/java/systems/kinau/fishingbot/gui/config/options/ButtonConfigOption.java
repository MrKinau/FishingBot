package systems.kinau.fishingbot.gui.config.options;

import javafx.scene.control.Button;

public class ButtonConfigOption extends ConfigOption {

    public ButtonConfigOption(String key, String text, boolean active) {
        super(key, text, null);
        Button button = new Button(text);
        button.setDisable(!active);
        getChildren().add(button);
    }
}
