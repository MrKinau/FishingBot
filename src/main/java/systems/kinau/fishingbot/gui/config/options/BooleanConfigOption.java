package systems.kinau.fishingbot.gui.config.options;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;

public class BooleanConfigOption extends ConfigOption {

    public BooleanConfigOption(String key, String description, boolean status) {
        this(key, description, status, null);
    }

    public BooleanConfigOption(String key, String description, boolean status, EventHandler<ActionEvent> onClick) {
        super(key, description, status);
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
