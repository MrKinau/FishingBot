/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot;

import org.apache.commons.cli.*;
import systems.kinau.fishingbot.gui.MainGUI;
import systems.kinau.fishingbot.gui.SwingDialogs;

import java.awt.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("nogui", false, "disables GUI");
        options.addOption("help", false, "shows help message");
        options.addOption("logsdir", true, "specifies where to save the logs");
        options.addOption("config", true, "specifies the path to the config");
        options.addOption("refreshToken", "accountfile", true, "specifies the path to the refreshToken which is used to login to Microsoft");
        options.addOption("onlyCreateConfig", false, "shut down the bot after the config is created");

        // add nogui option if Desktop is not supported
        if (!Desktop.isDesktopSupported()) {
            args = Arrays.copyOfRange(args, 0, args.length + 1);
            args[args.length - 1] = "-nogui";
        }

        CommandLineParser optionsParser = new DefaultParser();
        try {
            CommandLine cmd = optionsParser.parse(options, args);

            if (cmd.hasOption("help")) {
                new HelpFormatter().printHelp("FishingBot", options);
                return;
            }

            new FishingBot(cmd);

            if (cmd.hasOption("onlyCreateConfig"))
                return;

            if (!cmd.hasOption("nogui")) {
                String[] finalArgs = args;
                new Thread(() -> {
                    try {
//                        if (true) throw new IllegalArgumentException("simulate error");
                        new MainGUI(finalArgs);
                        if (FishingBot.getInstance().getCurrentBot() != null) {
                            FishingBot.getInstance().getCurrentBot().setPreventReconnect(true);
                            FishingBot.getInstance().getCurrentBot().setRunning(false);
                        }
                        FishingBot.getScheduler().shutdownNow();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        SwingDialogs.showJavaFXNotWorking(ex);
                    }
                }, "GUIThread").start();

            } else {
                FishingBot.getInstance().startBot();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
