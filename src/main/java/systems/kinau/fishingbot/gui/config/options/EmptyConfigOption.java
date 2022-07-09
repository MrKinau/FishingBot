package systems.kinau.fishingbot.gui.config.options;


import javafx.scene.control.Label;

public class EmptyConfigOption extends ConfigOption {
    public EmptyConfigOption(String key, String description) {
        super(key, description, null);
        Label emptyLabel = new Label(description);
        emptyLabel.setWrapText(true);
        emptyLabel.setMaxWidth(300);
        getChildren().add(emptyLabel);
    }
}
