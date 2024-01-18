package systems.kinau.fishingbot.gui.config.options;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import systems.kinau.fishingbot.gui.config.ConfigGUI;

public class BooleanConfigOption extends ConfigOption {

    public BooleanConfigOption(ConfigGUI configGui, String key, String description, boolean status) {
        this(configGui, key, description, status, null);
    }

    public BooleanConfigOption(ConfigGUI configGui, String key, String description, boolean status, EventHandler<ActionEvent> onClick) {
        super(configGui, key, description, status);
        CheckBox checkBox = new CheckBox(description);
        checkBox.setSelected(status);
        checkBox.setOnAction(event -> setValue(checkBox.isSelected()));
        if (onClick != null)
            checkBox.setOnAction(event -> {
                setValue(checkBox.isSelected());
                onClick.handle(event);
            });
        getChildren().add(checkBox);
    }

}
