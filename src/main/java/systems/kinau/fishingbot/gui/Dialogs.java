package systems.kinau.fishingbot.gui;

import javafx.scene.control.Alert;
import systems.kinau.fishingbot.FishingBot;

import javax.swing.*;
import java.util.List;

public class Dialogs {

    public static void showCredentialsNotSet() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(FishingBot.PREFIX);
        alert.setHeaderText("Your credentials are not set yet");
        alert.setContentText("Please set your credentials in the config.json\n" +
                "You can also use this bot in offline-mode. Set the online-mode to false and the account.mail to your desired username.");
        alert.showAndWait();
    }

    public static void showJavaFXNotWorking() {
        JOptionPane.showConfirmDialog(new JFrame(), "JavaFX is not working correctly on you environment." +
                        " Please see the error log.\n" +
                        " You can still use the bot in headless (nogui) mode using the start-argument -nogui.",
                "FishingBot", JOptionPane.DEFAULT_OPTION);
    }

    public static void showRealmsWorlds(List<String> possibleWorldsText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(FishingBot.PREFIX);
        alert.setContentText(String.join("\n", possibleWorldsText));
        alert.showAndWait();
    }

    public static void showRealmsAcceptToS() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(FishingBot.PREFIX);
        alert.setHeaderText("Accept ToS");
        alert.setContentText("If you want to use realms you have to accept the tos in the config.json");
        alert.showAndWait();
    }
}
