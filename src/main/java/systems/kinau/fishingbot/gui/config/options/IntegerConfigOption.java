package systems.kinau.fishingbot.gui.config.options;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

public class IntegerConfigOption extends ConfigOption {

    public IntegerConfigOption(String key, String description, int value) {
        this(key, description, value, null);
    }

    public IntegerConfigOption(String key, String description, int value, ChangeListener<Integer> onClick) {
        super(key, description, value);
        Label nameLabel = new Label(description);
        Spinner<Integer> spinner = new Spinner<>(Integer.MIN_VALUE, Integer.MAX_VALUE, value);
        spinner.setEditable(true);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> setValue(newValue));
        if (onClick != null)
            spinner.valueProperty().addListener(onClick);
        getChildren().addAll(nameLabel, spinner);
    }

}
