package systems.kinau.fishingbot.gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;

@NoArgsConstructor
public class MainGUI extends Application {

    public MainGUI(String[] args) {
        FishingBot.getInstance().setMainGUI(this);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fishingbot.fxml"), FishingBot.getI18n().getBundle());
        Parent root = loader.load();
        setStyle(root.getStylesheets());
        
        stage.setTitle("FishingBot");
        stage.getIcons().add(new Image(MainGUI.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));
        stage.setScene(new Scene(root, 600, 500));
        stage.setMinHeight(500);
        stage.setMinWidth(600);
        stage.show();

        // init logger
        FishingBot.getLog().addHandler(new GUILogHandler((TextArea) loader.getNamespace().get("consoleTextArea")));

        // Scene Builder does not accept this as fxml
        ((Accordion)loader.getNamespace().get("enchantmentsAccordion")).setExpandedPane((TitledPane)loader.getNamespace().get("booksPane"));
        ((Tab) loader.getNamespace().get("lootTab")).setText(FishingBot.getI18n().t("ui-tabs-loot", 0));

        FishingBot.getInstance().setMainGUIController(loader.getController());
    }

    @Override
    public void stop() throws Exception {
        FishingBot.getInstance().interruptMainThread();
    }

    public static void setStyle(ObservableList<String> stylesheets) {
        if (!stylesheets.contains("mainstyle.css"))
            stylesheets.add("mainstyle.css");
        if (FishingBot.getInstance().getConfig().getTheme().isDarkMode()) {
            if (!stylesheets.contains("darkstyle.css"))
                stylesheets.add("darkstyle.css");
        } else {
            stylesheets.remove("darkstyle.css");
        }
    }
}
