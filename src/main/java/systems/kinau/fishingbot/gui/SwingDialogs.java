package systems.kinau.fishingbot.gui;

import systems.kinau.fishingbot.FishingBot;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class SwingDialogs {
    public static void showJavaFXNotWorking(Throwable ex) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea label = new JTextArea("JavaFX seems to be not working properly on your computer!\n" +
                "JavaFX is required for the GUI to work. Please look at the log.\n\n" +
                "If you need help, click on \"Copy error for Discord\" and just ask for help in the Discord at\n" +
                "https://discord.gg/xHpCDYf\n\n" +
                "You can still use the bot in headless (nogui) mode using the start argument -nogui.");
        label.setMargin(new java.awt.Insets(0, 0, 20, 0));
        label.setBackground(new java.awt.Color(0, 0, 0, 0));
        label.setEditable(false);

        List<String> errorMsg = new ArrayList<>();
        errorMsg.add("java.version: " + System.getProperty("java.version"));
        errorMsg.add("java.vm.name: " + System.getProperty("java.vm.name"));
        errorMsg.add("java.runtime.name: " + System.getProperty("java.runtime.name"));
        errorMsg.add("java.runtime.version: " + System.getProperty("java.runtime.version"));
        errorMsg.add("java.vm.vendor: " + System.getProperty("java.vm.vendor"));
        errorMsg.add("os.name: " + System.getProperty("os.name"));
        errorMsg.add("os.version: " + System.getProperty("os.version"));
        errorMsg.add("os.arch: " + System.getProperty("os.arch"));
        errorMsg.add("fishingbot.version: " + FishingBot.TITLE);
        errorMsg.add("");
        errorMsg.add(ex.getClass().getName() + ": " + ex.getMessage());
        for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
            errorMsg.add("\t" + stackTraceElement.toString());
        }
        String errorMsgStr = String.join("\n", errorMsg);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton copyButton = new JButton("Copy error to clipboard");
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(errorMsgStr);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            if (copyButton.getText().startsWith("Copied"))
                copyButton.setText(copyButton.getText() + " again");
            else
                copyButton.setText("Copied");
        });
        copyButton.setMinimumSize(copyButton.getPreferredSize());
        copyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(copyButton);

        JButton copyDCButton = new JButton("Copy error for Discord");
        copyDCButton.addActionListener(e -> {
            // Discords limits are weird, message won't send although UI shows it fits into character limit
            StringSelection stringSelection = new StringSelection("```" + errorMsgStr.substring(0, Math.min(1800, errorMsgStr.length())) + "```");
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            copyDCButton.setText("Copied! Now paste your clipboard to the Discords fishingbot channel");
        });
        copyDCButton.setMinimumSize(copyDCButton.getPreferredSize());
        copyDCButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(copyDCButton);

        JTextArea textArea = new JTextArea(errorMsgStr);
        textArea.setEditable(false);
        textArea.setSize(textArea.getPreferredSize().width, textArea.getPreferredSize().height);

        panel.add(label);
        panel.add(buttonPanel);
        panel.add(textArea);
        JOptionPane.showConfirmDialog(null, panel, "FishingBot", JOptionPane.DEFAULT_OPTION);
    }
}
