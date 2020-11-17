package systems.kinau.fishingbot.gui.config.options;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import lombok.Getter;

import java.util.Arrays;

public class EnumConfigOption extends ConfigOption {

    @Getter Enum[] data;

    public EnumConfigOption(String key, String description, String current, Enum[] data) {
        this(key, description, current, data, null);
    }

    public EnumConfigOption(String key, String description, String current, Enum[] data, EventHandler<ActionEvent> onClick) {
        super(key, description, Arrays.stream(data).filter(anEnum -> anEnum.name().equals(current)).findAny().orElse(data[0]));
        this.data = data;
        Label nameLabel = new Label(description);
        ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(Arrays.asList(data)));
        choiceBox.getSelectionModel().select(getValue());
        choiceBox.setOnAction(event -> setValue(choiceBox.getSelectionModel().getSelectedItem()));
        if (onClick != null)
            choiceBox.setOnAction(onClick);
        getChildren().addAll(nameLabel, choiceBox);
    }

}
