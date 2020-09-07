/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot;

import org.apache.commons.cli.*;
import systems.kinau.fishingbot.gui.Dialogs;
import systems.kinau.fishingbot.gui.MainGUI;

import java.awt.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("nogui", false, "disables GUI");
        options.addOption("help", false, "shows help message");
        options.addOption("logsdir", true, "specifies where to save the logs");
        options.addOption("config", true, "specifies the path to the config");

        // add nogui option if Desktop is not supported
        if (!Desktop.isDesktopSupported()) {
            args = Arrays.copyOfRange(args, 0, args.length + 1);
            args[args.length - 1] = "nogui";
        }

        CommandLineParser optionsParser = new DefaultParser();
        try {
            CommandLine cmd = optionsParser.parse(options, args);

            if (cmd.hasOption("help")) {
                new HelpFormatter().printHelp("FishingBot", options);
                return;
            }

            if (!cmd.hasOption("nogui")) {
                String[] finalArgs = args;
                new Thread(() -> {
                    try {
                        try { Thread.sleep(200); } catch (InterruptedException ignore) { }
                        new MainGUI(finalArgs);
                        System.exit(0);
                    } catch (NoClassDefFoundError ex) {
                        Dialogs.showJavaFXNotWorking();
                    }
                }, "GUIThread").start();
            }
            FishingBot bot = new FishingBot(cmd);
            bot.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
