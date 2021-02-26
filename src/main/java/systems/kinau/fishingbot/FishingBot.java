package systems.kinau.fishingbot;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;
import systems.kinau.fishingbot.event.custom.BotStartEvent;
import systems.kinau.fishingbot.event.custom.BotStopEvent;
import systems.kinau.fishingbot.gui.GUIController;
import systems.kinau.fishingbot.gui.MainGUI;
import systems.kinau.fishingbot.i18n.I18n;
import systems.kinau.fishingbot.i18n.Language;
import systems.kinau.fishingbot.io.config.SettingsConfig;
import systems.kinau.fishingbot.io.logging.CustomPrintStream;
import systems.kinau.fishingbot.io.logging.LogFormatter;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FishingBot {

    @Getter @Setter private static I18n i18n;
    public static String PREFIX;
    @Getter private static FishingBot instance;
    @Getter public static Logger log = Logger.getLogger(Bot.class.getSimpleName());
    @Getter private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Getter private SettingsConfig config;
    private CommandLine cmdLine;

    @Getter @Setter private Bot currentBot;
    @Getter @Setter private MainGUI mainGUI;
    @Getter @Setter private GUIController mainGUIController;


    public FishingBot(CommandLine cmdLine) {
        instance = this;
        this.cmdLine = cmdLine;

        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("fishingbot.properties"));
            PREFIX = properties.getProperty("name") + " v" + properties.getProperty("version") + " - ";
        } catch (Exception ex) {
            PREFIX = "FishingBot vUnknown - ";
            ex.printStackTrace();
        }

        // initialize Logger
        log.setLevel(Level.ALL);
        ConsoleHandler ch;
        log.addHandler(ch = new ConsoleHandler());
        log.setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ch.setFormatter(formatter);
        CustomPrintStream.enableForPackage("systems.kinau.fishingbot", getLog());

        // start message
        getLog().info("Using " + PREFIX.substring(0, PREFIX.length() - 3));

        // i18n pre init

        i18n = new I18n(Language.ENGLISH, PREFIX, true);

        if (cmdLine.hasOption("config"))
            this.config = new SettingsConfig(cmdLine.getOptionValue("config"));
        else
            this.config = new SettingsConfig("config.json");

        i18n = new I18n(config.getLanguage(), PREFIX);
    }

    public void startBot() {
        if (getCurrentBot() != null)
            stopBot(true);
        Thread.currentThread().setName("mainThread");
        Bot bot = new Bot(cmdLine);
        bot.getEventManager().callEvent(new BotStartEvent());
        bot.start();
    }

    public void stopBot(boolean preventReconnect) {
        if (getCurrentBot() == null)
            return;
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new BotStopEvent());
        // TODO: Send Disconnect Packet
        getCurrentBot().setPreventReconnect(preventReconnect);
        getCurrentBot().setRunning(false);
    }

}
