package systems.kinau.fishingbot.gui;

import javax.swing.*;
import java.util.List;

public class Dialogs {

    public static void showCredentialsNotSet() {
        JOptionPane.showConfirmDialog(new JFrame(), "Your credentials are not set yet." +
                        " Please set them in the config.json\n" +
                        "You can also use it in offline-mode. Set the online-mode to false and the account.mail to your desired username.",
                "FishingBot", JOptionPane.DEFAULT_OPTION);
    }

    public static void showJavaFXNotWorking() {
        JOptionPane.showConfirmDialog(new JFrame(), "JavaFX is not working correctly on you environment." +
                        " Please see the error log.\n" +
                        " You can still use the bot in headless (nogui) mode using the start-argument -nogui.",
                "FishingBot", JOptionPane.DEFAULT_OPTION);
    }

    public static void showRealmsWorlds(List<String> possibleWorldsText) {
        JOptionPane.showConfirmDialog(new JFrame(), String.join("\n", possibleWorldsText), "FishingBot", JOptionPane.DEFAULT_OPTION);
    }

    public static void showRealmsAcceptToS() {
        JOptionPane.showConfirmDialog(new JFrame(),
                "If you want to use realms you have to accept the tos in the config.properties",
                "FishingBot", JOptionPane.DEFAULT_OPTION);
    }
}
