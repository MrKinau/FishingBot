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

@Getter
public class ConfigManager {

    private File file;

    private String serverIP = "127.0.0.1";
    private int serverPort = 25565;

    private boolean onlineMode = true;

    private String userName = "FishingBot";
    private String password = "CHANGEME";

    private int logCount = 15;
    private AnnounceType announceType = AnnounceType.ONLY_ENCHANTED;
    private List<String> startText = Arrays.asList("%prefix%Starting fishing", "/trigger Bot");

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
                if(!hasAllProperties(properties)) {
                    FishingBot.getLog().warning("Wrong config! Restoring to default.");
                    generateConfig();
                    return;
                }
                this.serverIP = properties.getProperty("server-ip");
                this.serverPort = Integer.valueOf(properties.getProperty("server-port"));
                this.onlineMode = Boolean.valueOf(properties.getProperty("online-mode"));
                this.userName = properties.getProperty("account-username");
                this.password = properties.getProperty("account-password");
                this.logCount = Integer.valueOf(properties.getProperty("log-count"));
                this.announceType = AnnounceType.valueOf(properties.getProperty("announce-type").toUpperCase());
                this.webHook = properties.getProperty("discord-webHook");
                this.startText = Arrays.asList(properties.getProperty("start-text").split(";"));
                this.defaultProtocol = ProtocolConstants.getProtocolId(properties.getProperty("default-protocol"));
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                FishingBot.getLog().severe("The given port is out of range!");
            }
        }
    }

    private boolean hasAllProperties(Properties props) {
        List<String> expectedProps = Arrays.asList("server-ip", "server-port", "online-mode", "account-username", "account-password", "log-count", "announce-type", "discord-webHook", "start-text", "default-protocol");
        long included = expectedProps.stream().filter(props::containsKey).count();
        return included == expectedProps.size();
    }

    private void generateConfig() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("server-ip", "127.0.0.1");
        properties.setProperty("server-port", "25565");
        properties.setProperty("online-mode", "true");
        properties.setProperty("account-username", "FishingBot");
        properties.setProperty("account-password", "CHANGEME");
        properties.setProperty("log-count", "15");
        properties.setProperty("announce-type", "ONLY_ENCHANTED");
        properties.setProperty("discord-webHook", "false");
        properties.setProperty("start-text", "%prefix%Starting fishing;/trigger Bot");
        properties.setProperty("default-protocol", ProtocolConstants.getVersionString(ProtocolConstants.MINECRAFT_1_8));
        String comments = "server-ip:\tServer IP the bot connects to\n" +
                "#server-port:\tPort of the server the bot connects to\n" +
                "#online-mode:\tToggles online-mode\n" +
                "#log-count:\tThe number of logs the bot generate\n" +
                "#announce-type:\tThe type of chat announcement:\n" +
                "#\tALL:\tAnnounces everything caught\n" +
                "#\tALL_BUT_FISH:\tAnnounces everything excepts fish\n" +
                "#\tONLY_ENCHANTED:\tAnnounces only enchanted stuff\n" +
                "#\tONLY_BOOKS:\tAnnounces only enchanted books\n" +
                "#\tNONE:\tAnnounces nothing\n" +
                "#discord-webHook:\tUse this to send all chat messages from the bot to a Discord webhook\n" +
                "#start-text:\tChat messages/commands separated with a semicolon\n" +
                "#account-username:\tThe username / e-mail of the account\n" +
                "#account-password:\tThe password of the account (ignored in offline-mode)\n" +
                "#default-protocol:\tOnly needed for Multi-Version servers. The Minecraft-Version for the ping request to the server. Possible values: (1.8, 1.9, 1.9.2, 1.9.2, 1.9.4, ...)\n";
        properties.store(new FileOutputStream(file), comments);
        FishingBot.getLog().info("Created new config.properties");
    }
}
