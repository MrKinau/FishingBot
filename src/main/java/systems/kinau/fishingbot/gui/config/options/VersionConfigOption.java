package systems.kinau.fishingbot.gui.config.options;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.util.stream.Collectors;

public class VersionConfigOption extends ConfigOption {

    public VersionConfigOption(String key, String description, String current) {
        this(key, description, current, null);
    }

    public VersionConfigOption(String key, String description, String current, EventHandler<ActionEvent> onClick) {
        super(key, description, current);
        Label nameLabel = new Label(description);
        ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(ProtocolConstants.SUPPORTED_VERSION_IDS.stream().map(ProtocolConstants::getVersionString).collect(Collectors.toList())));
        choiceBox.getSelectionModel().select(current);
        choiceBox.setOnAction(event -> setValue(choiceBox.getSelectionModel().getSelectedItem().toString()));
        if (onClick != null)
            choiceBox.setOnAction(onClick);
        getChildren().addAll(nameLabel, choiceBox);
    }

}
