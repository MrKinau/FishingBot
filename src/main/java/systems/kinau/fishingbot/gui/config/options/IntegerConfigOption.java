package systems.kinau.fishingbot.gui.config.options;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import systems.kinau.fishingbot.gui.config.ConfigGUI;

public class IntegerConfigOption extends ConfigOption {

    private Spinner<Integer> spinner;

    public IntegerConfigOption(ConfigGUI configGui, String key, String description, int value) {
        this(configGui, key, description, value, null);
    }

    public IntegerConfigOption(ConfigGUI configGui, String key, String description, int value, ChangeListener<Integer> onClick) {
        super(configGui, key, description, value);
        Label nameLabel = new Label(description);
        spinner = new Spinner<>(Integer.MIN_VALUE, Integer.MAX_VALUE, value);
        spinner.setEditable(true);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> setValue(newValue));
        if (onClick != null)
            spinner.valueProperty().addListener(onClick);
        getChildren().addAll(nameLabel, spinner);
    }

    @Override
    public void updateValue() {
        if (spinner == null)
            return;
        if (!spinner.isEditable())
            return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<Integer> converter = valueFactory.getConverter();
            if (converter != null) {
                Integer value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }
}
