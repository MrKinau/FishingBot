/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot;

import lombok.Getter;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.auth.Authenticator;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.fishing.ItemHandler;
import systems.kinau.fishingbot.io.ConfigManager;
import systems.kinau.fishingbot.io.LogFormatter;
import systems.kinau.fishingbot.io.discord.DiscordMessageDispatcher;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.handshake.HandshakeModule;
import systems.kinau.fishingbot.network.login.LoginModule;
import systems.kinau.fishingbot.network.ping.ServerPinger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FishingBot {

    @Getter static Logger log = Logger.getLogger(FishingBot.class.getSimpleName());
    @Getter static ConfigManager config;
    @Getter static DiscordMessageDispatcher discord;

    private String[] args;

    @Getter private boolean running;
    @Getter private Socket socket;
    @Getter private NetworkHandler net;

    @Getter private AuthData authData;

    @Getter private FishingManager fishingManager;

    private File logsFolder = new File("logs");

    public FishingBot(String[] args) {
        this.args = args;

        //Initialize Logger
        log.setLevel(Level.ALL);
        try {
            FileHandler fh;
            ConsoleHandler ch;
            if(!logsFolder.exists() && !logsFolder.mkdir() && logsFolder.isDirectory())
                throw new IOException("Could not create logs folder!");
            log.addHandler(fh = new FileHandler("logs/log%g.log", 0, 15));
            log.addHandler(ch = new ConsoleHandler());
            log.setUseParentHandlers(false);
            LogFormatter formatter = new LogFormatter();
            fh.setFormatter(formatter);
            ch.setFormatter(formatter);
        } catch (IOException e) {
            System.err.println("Could not create log!");
            System.exit(1);
        }

        config = new ConfigManager(new File("config.properties"));

        //Authenticate player if online-mode is set
        if(getConfig().isOnlineMode())
            authenticate();
        else
            this.authData = new AuthData(null, null, null, getConfig().getUserName());

        //Ping server
        ServerPinger sp = new ServerPinger(getConfig().getServerIP(), getConfig().getServerPort());
        sp.ping();

        //Activate Discord webHook
        if(!getConfig().getWebHook().equalsIgnoreCase("false"))
            discord = new DiscordMessageDispatcher(getConfig().getWebHook());
    }

    public void start() {
        if(isRunning())
            return;
        this.running = true;
        connect();
    }

    private boolean authenticate() {
        Authenticator authenticator = new Authenticator(getConfig().getUserName(), getConfig().getPassword());
        AuthData authData = authenticator.authenticate();
        if(authData == null) {
            this.authData = new AuthData(null, null, null, getConfig().getUserName());
            return false;
        }
        this.authData = authData;
        return true;
    }

    private void connect() {
        String serverName = getConfig().getServerIP();
        int port = getConfig().getServerPort();

        try {
            this.socket = new Socket(serverName, port);

            this.fishingManager = new FishingManager();
            this.net = new NetworkHandler(socket, authData, fishingManager);

            new HandshakeModule(serverName, port, getNet()).perform();
            new LoginModule(getAuthData().getUsername(), getNet()).perform();
            new ItemHandler();

            while (running) {
                try {
                    net.readData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    getLog().warning("Could not receive packet! Shutting down!");
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            getLog().severe("Could not start bot: IOException");
        }
    }
}
