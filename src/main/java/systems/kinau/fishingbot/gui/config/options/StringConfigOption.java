package systems.kinau.fishingbot.gui.config.options;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import systems.kinau.fishingbot.gui.config.ConfigGUI;

public class StringConfigOption extends ConfigOption {

    public StringConfigOption(ConfigGUI configGui, String key, String description, String text, boolean password) {
        this(configGui, key, description, text, password, null);
    }

    public StringConfigOption(ConfigGUI configGui, String key, String description, String text, boolean password, ChangeListener<String> onClick) {
        super(configGui, key, description, text);
        Label nameLabel = new Label(description);
        TextField textField = password ? new PasswordField() : new TextField();
        textField.setText(text);
        textField.textProperty().addListener((observable, oldValue, newValue) -> setValue(newValue));
        if (onClick != null)
            textField.textProperty().addListener(onClick);
        getChildren().addAll(nameLabel, textField);
    }

}
