package systems.kinau.fishingbot.gui.config.options;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.gui.MainGUI;
import systems.kinau.fishingbot.gui.Theme;
import systems.kinau.fishingbot.gui.config.ConfigGUI;
import systems.kinau.fishingbot.gui.config.DisplayNameConverter;
import systems.kinau.fishingbot.gui.config.DisplayNameProvider;

import java.util.Arrays;

public class EnumConfigOption extends ConfigOption {

    @Getter Enum[] data;

    public EnumConfigOption(ConfigGUI configGui, String key, String description, String current, Enum[] data) {
        this(configGui, key, description, current, data, null);
    }

    public EnumConfigOption(ConfigGUI configGui, String key, String description, String current, Enum[] data, EventHandler<ActionEvent> onClick) {
        super(configGui, key, description, Arrays.stream(data).filter(anEnum -> anEnum.name().equals(current)).findAny().orElse(data[0]));
        this.data = data;
        Label nameLabel = new Label(description);
        ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(Arrays.asList(data)));
        if (data[0] instanceof DisplayNameProvider) {
            choiceBox.setConverter(new DisplayNameConverter());
        }
        choiceBox.getSelectionModel().select(getValue());
        choiceBox.setOnAction(event -> {
            setValue(choiceBox.getSelectionModel().getSelectedItem());
            if (getKey().equals("misc.theme")) {
                FishingBot.getInstance().getConfig().setTheme((Theme) choiceBox.getSelectionModel().getSelectedItem());
                Parent root = choiceBox.getScene().getRoot();
                MainGUI.setStyle(root.getStylesheets());
                MainGUI.setStyle(getConfigGui().owner.getScene().getRoot().getStylesheets());
            }
        });
        if (onClick != null)
            choiceBox.setOnAction(onClick);
        getChildren().addAll(nameLabel, choiceBox);
    }

}
