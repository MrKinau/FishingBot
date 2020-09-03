package systems.kinau.fishingbot.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MainGUI extends Application {

    public MainGUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fishingbot.fxml"));
        stage.setTitle("FishingBot");
        stage.getIcons().add(new Image(MainGUI.class.getClassLoader().getResourceAsStream("icon.png")));
        stage.setScene(new Scene(root, 600, 500));
        stage.setMinHeight(500);
        stage.setMinWidth(600);
        stage.show();
    }

}
