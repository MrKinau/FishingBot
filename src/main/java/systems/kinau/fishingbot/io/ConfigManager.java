/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.io;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

//TODO: very messed up, use a better config API

@Getter
public class ConfigManager {

    private File file;

    private String serverIP = "127.0.0.1";
    private int serverPort = 25565;

    private boolean onlineMode = true;

    private String userName = "FishingBot";
    private String password = "CHANGEME";

    private int logCount = 15;
    private AnnounceType announceTypeChat = AnnounceType.ONLY_ENCHANTED;
    private AnnounceType announceTypeConsole = AnnounceType.ALL;
    private String announceLvlUp = "I've got a new level: %lvl%";
    private List<String> startText = Arrays.asList("%prefix%Starting fishing", "/trigger Bot");
    private boolean proxyChat = false;

    private int defaultProtocol = ProtocolConstants.MINECRAFT_1_8;

    private String webHook = "false";

    public ConfigManager(File file) {
        this.file = file;
        if(!file.exists()) {
            try {
                generateConfig();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(file));
                if(properties.containsKey("server-ip"))
                    this.serverIP = properties.getProperty("server-ip");
                if(properties.containsKey("server-port"))
                    this.serverPort = Integer.valueOf(properties.getProperty("server-port"));
                if(properties.containsKey("online-mode"))
                    this.onlineMode = Boolean.valueOf(properties.getProperty("online-mode"));
                if(properties.containsKey("account-username"))
                    this.userName = properties.getProperty("account-username");
                if(properties.containsKey("account-password"))
                    this.password = properties.getProperty("account-password");
                if(properties.containsKey("log-count"))
                    this.logCount = Integer.valueOf(properties.getProperty("log-count"));
                if(properties.containsKey("announce-type"))
                    this.announceTypeChat = AnnounceType.valueOf(properties.getProperty("announce-type").toUpperCase());
                if(properties.containsKey("announce-type-chat"))
                    this.announceTypeChat = AnnounceType.valueOf(properties.getProperty("announce-type-chat").toUpperCase());
                if(properties.containsKey("announce-type-console"))
                    this.announceTypeConsole = AnnounceType.valueOf(properties.getProperty("announce-type-console").toUpperCase());
                if(properties.containsKey("discord-webHook"))
                    this.webHook = properties.getProperty("discord-webHook");
                if(properties.containsKey("start-text"))
                    this.startText = Arrays.asList(properties.getProperty("start-text").split(";"));
                if(properties.containsKey("default-protocol"))
                    this.defaultProtocol = ProtocolConstants.getProtocolId(properties.getProperty("default-protocol"));
                if(properties.containsKey("announce-lvl-up"))
                    this.announceLvlUp = properties.getProperty("announce-lvl-up");
                if(properties.containsKey("proxy-chat"))
                    this.proxyChat = Boolean.valueOf(properties.getProperty("proxy-chat"));
                if(!hasAllProperties(properties)) {
                    FishingBot.getLog().warning("Wrong config! Updating config.");
                    generateConfig();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                FishingBot.getLog().severe("The given port is out of range!");
            }
        }
    }

    private boolean hasAllProperties(Properties props) {
        List<String> expectedProps = Arrays.asList("server-ip", "server-port", "online-mode", "account-username", "account-password", "log-count", "announce-type-chat", "announce-type-console", "discord-webHook", "start-text", "default-protocol", "announce-lvl-up", "proxy-chat");
        long included = expectedProps.stream().filter(props::containsKey).count();
        return included == expectedProps.size();
    }

    private void generateConfig() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("server-ip", getServerIP());
        properties.setProperty("server-port", String.valueOf(getServerPort()));
        properties.setProperty("online-mode", isOnlineMode() ? "true" : "false");
        properties.setProperty("account-username", getUserName());
        properties.setProperty("account-password", getPassword());
        properties.setProperty("log-count", String.valueOf(getLogCount()));
        properties.setProperty("announce-type-chat", getAnnounceTypeChat().toString());
        properties.setProperty("announce-type-console", getAnnounceTypeConsole().toString());
        properties.setProperty("discord-webHook", getWebHook());
        properties.setProperty("start-text", getStartText().toString().replace("[", "").replace("]", "").replace(", ",";"));   //Not clean
        properties.setProperty("default-protocol", ProtocolConstants.getVersionString(getDefaultProtocol()));
        properties.setProperty("announce-lvl-up", getAnnounceLvlUp());
        properties.setProperty("proxy-chat", Boolean.toString(proxyChat));
        String comments = "server-ip:\tServer IP the bot connects to\n" +
                "#server-port:\tPort of the server the bot connects to\n" +
                "#online-mode:\tToggles online-mode\n" +
                "#log-count:\tThe number of logs the bot generate\n" +
                "#announce-type-chat:\tThe type of chat announcement:\n" +
                "#announce-type-console:\tThe type of console log announcement:\n" +
                "# Announcement levels:\n"+
                "#\tALL:\tAnnounces everything caught\n" +
                "#\tALL_BUT_FISH:\tAnnounces everything excepts fish\n" +
                "#\tONLY_ENCHANTED:\tAnnounces only enchanted stuff\n" +
                "#\tONLY_BOOKS:\tAnnounces only enchanted books\n" +
                "#\tNONE:\tAnnounces nothing\n" +
                "#announce-lvl-up:\tText of the level-announcement in chat. %lvl% will be replaced with the gained level.\n" +
                "\tUse \"false\" if you dont want to announce the achieved levels\n" +
                "#discord-webHook:\tUse this to send all chat messages from the bot to a Discord webhook\n" +
                "#start-text:\tChat messages/commands separated with a semicolon\n" +
                "#account-username:\tThe username / e-mail of the account\n" +
                "#account-password:\tThe password of the account (ignored in offline-mode)\n" +
                "#default-protocol:\tOnly needed for Multi-Version servers. The Minecraft-Version for the ping request to the server. Possible values: (1.8, 1.9, 1.9.2, 1.9.2, 1.9.4, ...)\n" +
                "#proxy-chat:\tWhether to function as a chat client (printing incoming chat messages to the console and sending input as chat)";
        properties.store(new FileOutputStream(file), comments);
        FishingBot.getLog().info("Created/Updated config.properties");
    }
}
