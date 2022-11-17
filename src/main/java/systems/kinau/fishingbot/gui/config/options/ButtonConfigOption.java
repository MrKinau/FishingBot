package systems.kinau.fishingbot.gui.config.options;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ButtonConfigOption extends ConfigOption {

    public ButtonConfigOption(String key, String text, boolean active, EventHandler<ActionEvent> onAction) {
        super(key, text, null);
        Button button = new Button(text);
        button.setDisable(!active);
        button.setOnAction(onAction);
        getChildren().add(button);
    }
}
