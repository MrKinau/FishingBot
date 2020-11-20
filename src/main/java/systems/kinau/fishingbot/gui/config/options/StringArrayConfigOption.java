package systems.kinau.fishingbot.gui.config.options;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import systems.kinau.fishingbot.FishingBot;

import java.util.ArrayList;
import java.util.Optional;


public class StringArrayConfigOption extends ConfigOption {

    public StringArrayConfigOption(String key, String description, String[] values, Stage primaryStage) {
        super(key, description, values);

        Label nameLabel = new Label(description);
        Button editButton = new Button(FishingBot.getI18n().t("ui-menu-edit"));
        getChildren().addAll(nameLabel, editButton);

        editButton.setOnAction(event -> {
            ListView<String> listView = new ListView<>();
            if (getValue() instanceof String[]) {
                listView.getItems().addAll((String[]) getValue());
            }
            if (listView.getItems().size() > 0)
                listView.getSelectionModel().select(0);

            VBox controlsPane = new VBox(10);
            controlsPane.setPadding(new Insets(5));
            controlsPane.setAlignment(Pos.TOP_CENTER);

            Button addButton = new Button(FishingBot.getI18n().t("ui-button-add"));
            Button removeButton = new Button(FishingBot.getI18n().t("ui-button-remove"));
            if (listView.getItems().size() <= 0)
                removeButton.setDisable(true);

            addButton.setOnAction(event1 -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("FishingBot - Config");
                inputDialog.setHeaderText(FishingBot.getI18n().t("ui-array-input-header"));
                Optional<String> result = inputDialog.showAndWait();
                result.ifPresent(s -> {
                    listView.getItems().add(s);
                    setValue(new ArrayList<>(listView.getItems()).toArray(new String[0]));
                });
            });

            removeButton.setOnAction(event1 -> {
                int selected = listView.getSelectionModel().getSelectedIndex();
                listView.getItems().remove(selected);
                if (listView.getItems().size() <= 0)
                    removeButton.setDisable(true);
                setValue(new ArrayList<>(listView.getItems()).toArray(new String[0]));
            });

            listView.setOnMouseClicked(event1 -> {
                if (listView.getItems().size() > 0 && listView.getSelectionModel().getSelectedIndex() >= 0)
                    removeButton.setDisable(false);
            });

            controlsPane.getChildren().addAll(addButton, removeButton);

            HBox titleBox = new HBox();
            titleBox.setAlignment(Pos.TOP_CENTER);
            Label title = new Label(getDescription());
            title.setFont(new Font(18));
            title.setPadding(new Insets(5, 0, 5, 0));
            titleBox.getChildren().add(title);

            BorderPane rootPane = new BorderPane();
            rootPane.setTop(titleBox);
            rootPane.setCenter(listView);
            rootPane.setRight(controlsPane);

            Scene scene = new Scene(rootPane, 300, 400);

            Stage window = new Stage();
            window.setTitle("FishingBot - Config");
            window.getIcons().add(new Image(StringArrayConfigOption.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));
            window.setScene(scene);

            window.setMinHeight(150);
            window.setMinWidth(150);
            window.setHeight(400);
            window.setWidth(300);

            window.initModality(Modality.WINDOW_MODAL);
            window.initOwner(primaryStage);

            window.show();
        });
    }
}
