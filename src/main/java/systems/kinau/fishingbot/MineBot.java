/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.auth.Authenticator;
import systems.kinau.fishingbot.event.EventManager;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.fishing.ItemHandler;
import systems.kinau.fishingbot.io.LogFormatter;
import systems.kinau.fishingbot.io.SettingsConfig;
import systems.kinau.fishingbot.io.discord.DiscordMessageDispatcher;
import systems.kinau.fishingbot.mining.MiningManager;
import systems.kinau.fishingbot.mining.Player;
import systems.kinau.fishingbot.mining.World;
import systems.kinau.fishingbot.network.ping.ServerPinger;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.handshake.HandshakeModule;
import systems.kinau.fishingbot.network.protocol.login.LoginModule;
import systems.kinau.fishingbot.realms.RealmsAPI;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MineBot {

    public static final String PREFIX = "MineBot v2.4 - ";
    @Getter public static MineBot instance;
    @Getter public static Logger log = Logger.getLogger(MineBot.class.getSimpleName());
    @Getter @Setter public static boolean running;
    @Getter private static SettingsConfig config;
    @Getter private static DiscordMessageDispatcher discord;
    @Getter private static ChatHandler chatHandler;
    @Getter @Setter private static int serverProtocol = ProtocolConstants.MINECRAFT_1_8; //default 1.8
    @Getter @Setter private static String serverHost;
    @Getter @Setter private static int serverPort;
    @Getter @Setter private AuthData authData;
    @Getter @Setter private World world;
    @Getter @Setter private Player player;
    @Getter         private EventManager eventManager;

    private String[] args;

    @Getter private Socket socket;
    @Getter private NetworkHandler net;

    @Getter private Manager manager;

    private File logsFolder = new File("logs");
    private BotMode botMode = BotMode.FISHING;

    public MineBot(String[] args) {
        instance = this;

        this.args = args;

        //Load args
        if(args.length >= 1 && args[0].equalsIgnoreCase("mining"))
            this.botMode = BotMode.MINING;

        //Initialize Logger
        log.setLevel(Level.ALL);
        ConsoleHandler ch;
        log.addHandler(ch = new ConsoleHandler());
        log.setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ch.setFormatter(formatter);

        //Generate/Load config
        MineBot.config = new SettingsConfig();

        //Set logger file handler
        try {
            FileHandler fh;
            if(!logsFolder.exists() && !logsFolder.mkdir() && logsFolder.isDirectory())
                throw new IOException("Could not create logs folder!");
            log.addHandler(fh = new FileHandler("logs/log%g.log", 0 /* 0 = infinity */, getConfig().getLogCount()));
            fh.setFormatter(new LogFormatter());
        } catch (IOException e) {
            System.err.println("Could not create log!");
            System.exit(1);
        }

        //Load EventManager
        this.eventManager = new EventManager();

        //Authenticate player if online-mode is set
        if(getConfig().isOnlineMode())
            authenticate();
        else {
            getLog().info("Starting in offline-mode with username: " + getConfig().getUserName());
            this.authData = new AuthData(null, null, null, getConfig().getUserName());
        }

        String ip = getConfig().getServerIP();
        int port = getConfig().getServerPort();

        //Check rather to connect to realm
        if (getConfig().getRealmId() != -1) {
            RealmsAPI realmsAPI = new RealmsAPI(getAuthData());
            if (getConfig().getRealmId() == 0) {
                realmsAPI.printPossibleWorlds();
                MineBot.getLog().info("Shutting down, because realm-id is not set...");
                System.exit(0);
            }
            if (getConfig().isRealmAcceptTos())
                realmsAPI.agreeTos();
            else {
                MineBot.getLog().severe("*****************************************************************************");
                MineBot.getLog().severe("If you want to use realms you have to accept the tos in the config.properties");
                MineBot.getLog().severe("*****************************************************************************");
                System.exit(0);
            }

            String ipAndPort = null;
            for (int i = 0; i < 5; i++) {
                ipAndPort = realmsAPI.getServerIP(getConfig().getRealmId());
                if (ipAndPort == null) {
                    MineBot.getLog().info("Trying to receive IP (Try " + (i + 1) + ")...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    break;
            }
            if (ipAndPort == null)
                System.exit(0);
            ip = ipAndPort.split(":")[0];
            port = Integer.parseInt(ipAndPort.split(":")[1]);
        }

        //Ping server
        getLog().info("Pinging " + ip + ":" + port + " with protocol of MC-" + getConfig().getDefaultProtocol());
        ServerPinger sp = new ServerPinger(ip, port, this);
        sp.ping();

        //Activate Discord webHook
        if(!getConfig().getWebHook().equalsIgnoreCase("false"))
            MineBot.discord = new DiscordMessageDispatcher(getConfig().getWebHook());

        // Initalize chat message
        MineBot.chatHandler = new ChatHandler(this);
    }

    public void start() {
        if(isRunning())
            return;
        connect();
    }

    private boolean authenticate() {
        Authenticator authenticator = new Authenticator(getConfig().getUserName(), getConfig().getPassword());
        AuthData authData = authenticator.authenticate();
        if(authData == null) {
            setAuthData(new AuthData(null, null, null, getConfig().getUserName()));
            return false;
        }
        setAuthData(authData);
        return true;
    }

    private void connect() {
        String serverName = getServerHost();
        int port = getServerPort();

        do {
            try {
                setRunning(true);
                this.socket = new Socket(serverName, port);

                switch (botMode) {
                    case FISHING: this.manager = new FishingManager(); break;
                    case MINING: this.manager = new MiningManager(); break;
                }
                this.net = new NetworkHandler(socket);

                new HandshakeModule(serverName, port, getNet()).perform();
                new LoginModule(getAuthData().getUsername(), getNet()).perform();
                new ItemHandler(getServerProtocol());

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                while (running) {
                    try {
                        net.readData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getLog().warning("Could not receive packet! Shutting down!");
                        break;
                    }
                }
            } catch (IOException e) {
                getLog().severe("Could not start bot: " + e.getMessage());
            } finally {
                try {
                    if (socket != null)
                        this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.socket = null;
                if(getManager() != null)
                    getManager().shutdown();
                this.manager = null;
                this.world = null;
                this.net = null;
            }
            if (getConfig().isAutoReconnect()) {
                getLog().info("MineBot restarts in 3 seconds...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (getAuthData() == null) {
                    if (getConfig().isOnlineMode())
                        authenticate();
                    else {
                        getLog().info("Starting in offline-mode with username: " + getConfig().getUserName());
                        authData = new AuthData(null, null, null, getConfig().getUserName());
                    }
                }
            }
        } while (getConfig().isAutoReconnect());
    }
}
