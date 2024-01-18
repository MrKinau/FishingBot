package systems.kinau.fishingbot.gui.config.options;


import javafx.scene.control.Label;
import systems.kinau.fishingbot.gui.config.ConfigGUI;

public class EmptyConfigOption extends ConfigOption {
    public EmptyConfigOption(ConfigGUI configGui, String key, String description) {
        super(configGui, key, description, null);
        Label emptyLabel = new Label(description);
        emptyLabel.setWrapText(true);
        emptyLabel.setMaxWidth(300);
        getChildren().add(emptyLabel);
    }
}
