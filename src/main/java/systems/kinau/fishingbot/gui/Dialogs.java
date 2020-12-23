package systems.kinau.fishingbot.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import systems.kinau.fishingbot.FishingBot;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Dialogs {

    public static void showCredentialsNotSet() {
        setupJFX();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(FishingBot.PREFIX);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-credentials-header"));
            alert.setContentText(FishingBot.getI18n().t("dialog-credentials-content"));

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static void showJavaFXNotWorking() {
        JOptionPane.showConfirmDialog(new JFrame(), "JavaFX seems to be not working properly on your computer!\nPlease look at the log.\n\n" +
                "You can still use the bot in headless (nogui) mode using the start argument -nogui.", "FishingBot", JOptionPane.DEFAULT_OPTION);
    }

    public static void showRealmsWorlds(List<String> possibleWorldsText) {
        setupJFX();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.PREFIX);
            alert.setContentText(String.join("\n", possibleWorldsText));

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    public static void showRealmsAcceptToS() {
        setupJFX();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.PREFIX);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-realms-tos-header"));
            alert.setContentText(FishingBot.getI18n().t("dialog-realms-tos-content"));

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }

    private static void setupJFX() {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        try { latch.await(); } catch (InterruptedException ignore) { }
    }

    public static void showAboutWindow(Stage parent, Consumer<String> callBack) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(FishingBot.PREFIX);

            alert.setHeaderText(FishingBot.getI18n().t("dialog-about-header"));
            FlowPane fp = new FlowPane();
            Label lbl = new Label(FishingBot.getI18n().t("dialog-about-content"));
            Hyperlink link = new Hyperlink(" faithful.team");
            fp.getChildren().addAll( lbl, link);

            link.setOnAction(event -> {
                alert.close();
                callBack.accept("https://faithful.team/");
            });

            alert.getDialogPane().contentProperty().set(fp);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.initOwner(parent);
            stage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("img/items/fishing_rod.png")));

            alert.showAndWait();
        });
    }
}
