/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.OneSixParamStorage;
import org.apache.commons.cli.CommandLine;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.auth.Authenticator;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.bot.loot.LootHistory;
import systems.kinau.fishingbot.event.EventManager;
import systems.kinau.fishingbot.gui.Dialogs;
import systems.kinau.fishingbot.gui.GUIController;
import systems.kinau.fishingbot.i18n.I18n;
import systems.kinau.fishingbot.io.config.SettingsConfig;
import systems.kinau.fishingbot.io.logging.LogFormatter;
import systems.kinau.fishingbot.modules.*;
import systems.kinau.fishingbot.modules.command.ChatCommandModule;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.modules.command.CommandRegistry;
import systems.kinau.fishingbot.modules.command.commands.*;
import systems.kinau.fishingbot.modules.discord.DiscordModule;
import systems.kinau.fishingbot.modules.ejection.EjectionModule;
import systems.kinau.fishingbot.modules.fishing.FishingModule;
import systems.kinau.fishingbot.modules.fishing.RegistryHandler;
import systems.kinau.fishingbot.modules.timer.TimerModule;
import systems.kinau.fishingbot.network.ping.ServerPinger;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.realms.Realm;
import systems.kinau.fishingbot.network.realms.RealmsAPI;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;

public class Bot {

    @Getter @Setter private boolean running;
    @Getter @Setter private boolean preventStartup;
    @Getter @Setter private boolean preventReconnect;
    @Getter         private SettingsConfig config;
    @Getter @Setter private int serverProtocol = ProtocolConstants.MINECRAFT_1_8; //default 1.8
    @Getter @Setter private String serverHost;
    @Getter @Setter private int serverPort;
    @Getter @Setter private AuthData authData;
    @Getter @Setter private boolean wontConnect = false;
    @Getter         private ExecutorService commandsThread;

    @Getter         private EventManager eventManager;
    @Getter         private CommandRegistry commandRegistry;
    @Getter         private ModuleManager moduleManager;

    @Getter         private Player player;

    @Getter         private Socket socket;
    @Getter         private NetworkHandler net;

    @Getter @Setter private FishingModule fishingModule;

    @Getter         private File logsFolder = new File("logs");
    @Getter         private File accountFile = new File("account.json");

    public Bot(CommandLine cmdLine) {
        FishingBot.getInstance().setCurrentBot(this);
        this.eventManager = new EventManager();
        this.moduleManager = new ModuleManager();
        if (!cmdLine.hasOption("nogui"))
            getEventManager().registerListener(FishingBot.getInstance().getMainGUIController());

        // read config

        if (cmdLine.hasOption("config"))
            this.config = new SettingsConfig(cmdLine.getOptionValue("config"));
        else
            this.config = new SettingsConfig("config.json");

        // update i18n

        FishingBot.setI18n(new I18n(config.getLanguage(), FishingBot.PREFIX, true));

        // use command line arguments
        if (cmdLine.hasOption("logsdir")) {
            this.logsFolder = new File(cmdLine.getOptionValue("logsdir"));
            if (!logsFolder.exists()) {
                boolean success = logsFolder.mkdirs();
                if (!success) {
                    FishingBot.getI18n().severe("log-failed-creating-folder");
                    FishingBot.getInstance().getCurrentBot().setRunning(false);
                    FishingBot.getInstance().getCurrentBot().setWontConnect(true);
                    return;
                }
            }
        }

        if (cmdLine.hasOption("accountfile")) {
            this.accountFile = new File(cmdLine.getOptionValue("accountfile"));
        }

        // set logger file handler
        try {
            FileHandler fh;
            if(!logsFolder.exists() && !logsFolder.mkdir() && logsFolder.isDirectory())
                throw new IOException(FishingBot.getI18n().t("log-failed-creating-folder"));
            FishingBot.getLog().removeHandler(Arrays.stream(FishingBot.getLog().getHandlers()).filter(handler -> handler instanceof FileHandler).findAny().orElse(null));
            FishingBot.getLog().addHandler(fh = new FileHandler(logsFolder.getPath() + "/log%g.log", 0 /* 0 = infinity */, getConfig().getLogCount()));
            fh.setFormatter(new LogFormatter());
        } catch (IOException e) {
            FishingBot.getI18n().severe("log-failed-creating-log");
            FishingBot.getInstance().getCurrentBot().setRunning(false);
            FishingBot.getInstance().getCurrentBot().setWontConnect(true);
            return;
        }

        // log config location
        FishingBot.getI18n().info("config-loaded-from", new File(getConfig().getPath()).getAbsolutePath());

        // Ignore invalid config if the OneSixLauncher is used
        OneSixParamStorage oneSix = OneSixParamStorage.getInstance();
        if(oneSix == null) {
            // error if credentials are default credentials
            if (getConfig().getUserName().equals("my-minecraft@login.com")) {
                FishingBot.getI18n().warning("credentials-not-set");
                if (!cmdLine.hasOption("nogui"))
                    Dialogs.showCredentialsNotSet();
                setPreventStartup(true);
                return;
            }
        }

        // authenticate player if online-mode is set
        if (getConfig().isOnlineMode()) {
            boolean authSuccessful = authenticate(accountFile);
            if (!authSuccessful) {
                setPreventStartup(true);
                FishingBot.getI18n().severe("credentials-invalid");
                if (!cmdLine.hasOption("nogui")) {
                    Dialogs.showCredentialsInvalid(GUIController::openWebpage);
                }
            }
        } else {
            FishingBot.getI18n().info("credentials-using-offline-mode", getConfig().getUserName());
            this.authData = new AuthData(null, null, null, getConfig().getUserName());
        }

        if (!cmdLine.hasOption("nogui")) {
            FishingBot.getInstance().getMainGUIController().setImage(authData.getProfile());
            FishingBot.getInstance().getMainGUIController().setAccountName(authData.getUsername());
        }

        FishingBot.getI18n().info("auth-username", authData.getUsername());

        String ip = getConfig().getServerIP();
        int port = getConfig().getServerPort();

        //Check rather to connect to realm
        if (getConfig().getRealmId() != -1) {
            RealmsAPI realmsAPI = new RealmsAPI(getAuthData());
            if (getConfig().getRealmId() == 0) {
                List<Realm> possibleRealms = realmsAPI.getPossibleWorlds();
                realmsAPI.printRealms(possibleRealms);
                FishingBot.getI18n().info("realms-id-not-set");
                if (!cmdLine.hasOption("nogui")) {
                    AtomicBoolean dialogClicked = new AtomicBoolean(false);
                    Dialogs.showRealmsWorlds(possibleRealms, realm -> {
                        if (realm != null) {
                            FishingBot.getInstance().getConfig().setRealmId(realm.getId());
                            FishingBot.getInstance().getConfig().save();
                            getConfig().setRealmId(realm.getId());
                            getConfig().save();
                        }
                        dialogClicked.set(true);
                    });

                    // wait in this thread until the dialog is answered
                    while (!dialogClicked.get()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (getConfig().getRealmId() == 0) {
                    setPreventStartup(true);
                    return;
                }
            }
            if (getConfig().isRealmAcceptTos())
                realmsAPI.agreeTos();
            else {
                if (!cmdLine.hasOption("nogui")) {
                    AtomicBoolean dialogClicked = new AtomicBoolean(false);
                    Dialogs.showRealmsAcceptToS(clickedYes -> {
                        if (clickedYes) {
                            FishingBot.getInstance().getConfig().setRealmAcceptTos(true);
                            FishingBot.getInstance().getConfig().save();
                            getConfig().setRealmAcceptTos(true);
                            getConfig().save();
                        }
                        dialogClicked.set(true);
                    });

                    // wait in this thread until the dialog is answered
                    while (!dialogClicked.get()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!getConfig().isRealmAcceptTos()) {
                    FishingBot.getI18n().severe("realms-tos-agreement");
                    setPreventStartup(true);
                    return;
                }
            }

            String ipAndPort = null;
            for (int i = 0; i < 5; i++) {
                ipAndPort = realmsAPI.getServerIP(getConfig().getRealmId());
                if (ipAndPort == null) {
                    FishingBot.getI18n().info("realms-determining-address", String.valueOf(i + 1));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignore) { }
                } else
                    break;
            }
            if (ipAndPort == null) {
                setWontConnect(true);
                setRunning(false);
                setPreventReconnect(true);
                return;
            }
            ip = ipAndPort.split(":")[0];
            port = Integer.parseInt(ipAndPort.split(":")[1]);
        }

        //Ping server
        FishingBot.getI18n().info("server-pinging", ip, String.valueOf(port), getConfig().getDefaultProtocol());
        ServerPinger sp = new ServerPinger(ip, port);
        sp.ping();
    }

    public FishingModule getFishingModule() {
        return (FishingModule) getModuleManager().getLoadedModule(FishingModule.class).orElse(null);
    }

    public EjectionModule getEjectModule() {
        return (EjectionModule) getModuleManager().getLoadedModule(EjectionModule.class).orElse(null);
    }

    public DiscordModule getDiscordModule() {
        return (DiscordModule) getModuleManager().getLoadedModule(DiscordModule.class).orElse(null);
    }

    public void start(CommandLine cmdLine) {
        if (isRunning() || isPreventStartup()) {
            FishingBot.getInstance().setCurrentBot(null);
            if (cmdLine.hasOption("nogui"))
                return;
            FishingBot.getInstance().getMainGUIController().updateStartStop();
            FishingBot.getInstance().getMainGUIController().enableStartStop();
            return;
        }
        connect();
    }

    public void runCommand(String command, boolean executeBotCommand) {
        commandsThread.execute(() -> {
            if (getNet() == null)
                return;
            if (executeBotCommand && command.startsWith("/")) {
                boolean executed = FishingBot.getInstance().getCurrentBot().getCommandRegistry().dispatchCommand(command, CommandExecutor.CONSOLE);
                if (executed)
                    return;
            }

            getPlayer().sendMessage(command);
        });
    }

    private boolean authenticate(File accountFile) {
        Authenticator authenticator = new Authenticator(accountFile);
        Optional<AuthData> authData = authenticator.authenticate(getConfig().getAuthService());

        if (!authData.isPresent()) {
            setAuthData(new AuthData(null, null, null, getConfig().getUserName()));
            return false;
        }

        setAuthData(authData.get());
        return true;
    }

    private void registerCommands() {
        this.commandRegistry = new CommandRegistry();
        getCommandRegistry().registerCommand(new HelpCommand());
        getCommandRegistry().registerCommand(new LevelCommand());
        getCommandRegistry().registerCommand(new EmptyCommand());
        getCommandRegistry().registerCommand(new ByeCommand());
        getCommandRegistry().registerCommand(new StuckCommand());
        getCommandRegistry().registerCommand(new DropRodCommand());
        getCommandRegistry().registerCommand(new LookCommand());
        getCommandRegistry().registerCommand(new SummaryCommand());
        getCommandRegistry().registerCommand(new RightClickCommand());
        getCommandRegistry().registerCommand(new SwapCommand());
        getCommandRegistry().registerCommand(new ClickInvCommand());
        getCommandRegistry().registerCommand(new WaitCommand());
    }

    private void connect() {
        String serverName = getServerHost();
        int port = getServerPort();

        LootHistory savedLootHistory = new LootHistory();

        do {
            try {
                setRunning(true);
                if (isWontConnect()) {
                    setWontConnect(false);
                    ServerPinger sp = new ServerPinger(getServerHost(), getServerPort());
                    sp.ping();
                    if (isWontConnect()) {
                        if (!getConfig().isAutoReconnect())
                            return;
                        try {
                            Thread.sleep(getConfig().getAutoReconnectTime() * 1000);
                        } catch (InterruptedException ignore) { }
                        continue;
                    }
                }
                this.socket = new Socket(serverName, port);

                this.net = new NetworkHandler();
                this.commandsThread = Executors.newSingleThreadExecutor(
                        new ThreadFactoryBuilder().setNameFormat("command-executor-thread-%d").build());

                registerCommands();
                if (FishingBot.getInstance().getMainGUIController() != null)
                    getEventManager().registerListener(FishingBot.getInstance().getMainGUIController());

                if (FishingBot.getInstance().getMainGUIController() != null && !getEventManager().isRegistered(FishingBot.getInstance().getMainGUIController()))
                    getEventManager().registerListener(FishingBot.getInstance().getMainGUIController());

                // registry handler
                new RegistryHandler(getServerProtocol());

                // enable required modules

                getModuleManager().enableModule(new HandshakeModule(serverName, port));
                getModuleManager().enableModule(new LoginModule(getAuthData().getUsername()));
                getModuleManager().enableModule(new ClientDefaultsModule());
                getModuleManager().enableModule(new FishingModule(savedLootHistory));
                getModuleManager().enableModule(new ChatProxyModule());

                if (getConfig().isStartTextEnabled())
                    getModuleManager().enableModule(new ChatCommandModule());

                if (getConfig().isWebHookEnabled())
                    getModuleManager().enableModule(new DiscordModule());

                if (getConfig().isAutoLootEjectionEnabled())
                    getModuleManager().enableModule(new EjectionModule());

                if (getConfig().isTimerEnabled())
                    getModuleManager().enableModule(new TimerModule());

                // init player

                this.player = new Player();

                // add shutdown hook

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (socket != null && !socket.isClosed())
                            socket.close();
                        if (commandsThread != null && !commandsThread.isTerminated())
                            commandsThread.shutdownNow();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                // game loop (for receiving packets)

                while (running) {
                    try {
                        net.readData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FishingBot.getI18n().warning("packet-could-not-be-received");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                FishingBot.getI18n().severe("bot-could-not-be-started", e.getMessage());
            } finally {
                try {
                    if (socket != null)
                        this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (getPlayer() != null)
                    getEventManager().unregisterListener(getPlayer());
                commandsThread.shutdownNow();
                getEventManager().getRegisteredListener().clear();
                getEventManager().getClassToInstanceMapping().clear();
                if (getFishingModule() != null)
                    savedLootHistory = getFishingModule().getLootHistory();
                getModuleManager().disableAll();
                this.socket = null;
                this.net = null;
                this.player = null;
            }
            if (getConfig().isAutoReconnect() && !isPreventReconnect()) {
                FishingBot.getI18n().info("bot-automatic-reconnect", String.valueOf(getConfig().getAutoReconnectTime()));

                try {
                    Thread.sleep(getConfig().getAutoReconnectTime() * 1000);
                } catch (InterruptedException ignore) { }

                if (getAuthData() == null) {
                    if (getConfig().isOnlineMode())
                        authenticate(accountFile);
                    else {
                        FishingBot.getI18n().info("credentials-using-offline-mode", getConfig().getUserName());
                        authData = new AuthData(null, null, null, getConfig().getUserName());
                    }
                }
            }
        } while (getConfig().isAutoReconnect() && !isPreventReconnect());
        FishingBot.getInstance().setCurrentBot(null);
        if (FishingBot.getInstance().getMainGUIController() != null) {
            FishingBot.getInstance().getMainGUIController().updateStartStop();
            FishingBot.getInstance().getMainGUIController().updatePlayPaused();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) { }
            FishingBot.getInstance().getMainGUIController().enableStartStop();
        }
    }
}
