package systems.kinau.fishingbot.gui;

import javax.swing.*;

public class JavaFXNotWorkingGUI {

    public void show() {
        JOptionPane.showConfirmDialog(new JFrame(), "JavaFX is not working correctly on you environment." +
                " Please see the error log.\n" +
                " You can still use the bot in headless (nogui) mode using the start-argument -nogui.",
                "FishingBot", JOptionPane.DEFAULT_OPTION);
    }
}
