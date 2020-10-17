package systems.kinau.fishingbot.gui;

import javafx.scene.control.Alert;
import systems.kinau.fishingbot.FishingBot;

import javax.swing.*;
import java.util.List;

public class Dialogs {

    public static void showCredentialsNotSet() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(FishingBot.PREFIX);

        alert.setHeaderText(FishingBot.getI18n().t("dialog-credentials-header"));
        alert.setContentText(FishingBot.getI18n().t("dialog-credentials-content"));

        alert.showAndWait();
    }

    public static void showJavaFXNotWorking() {
        JOptionPane.showConfirmDialog(new JFrame(), FishingBot.getI18n().t("dialog-javafx-header") + "\n" + FishingBot.getI18n().t("dialog-javafx-content"), FishingBot.PREFIX, JOptionPane.DEFAULT_OPTION);
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

        alert.setHeaderText(FishingBot.getI18n().t("dialog-realms-tos-header"));
        alert.setContentText(FishingBot.getI18n().t("dialog-realms-tos-content"));

        alert.showAndWait();
    }
}
