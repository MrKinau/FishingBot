/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.io;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import systems.kinau.fishingbot.FishingBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Getter
public class ConfigManager {

    private File file;

    private String serverIP;
    private int serverPort;
    private boolean onlineMode;
    private String userName;
    private String password;


    public ConfigManager(File file) {
        Validate.notNull(file);
        this.file = file;
        if(!file.exists()) {
            try {
                Properties properties = new Properties();
                properties.setProperty("server-ip", "127.0.0.1");
                properties.setProperty("server-port", "25565");
                properties.setProperty("online-mode", "true");
                properties.setProperty("account-username", "FishingBot");
                properties.setProperty("account-password", "CHANGEME");
                String comments = "server-ip:\tServer IP the bot connects to\n" +
                        "#server-port:\tPort of the server the bot connects to\n" +
                        "#online-mode:\tToggles online-mode\n" +
                        "#account-username:\tThe username / e-mail of the account\n" +
                        "#account-password:\tThe password of the account (ignored in offline-mode)\n";
                properties.store(new FileOutputStream(file), comments);
                FishingBot.getLog().info("Created new config.properties");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(file));
                this.serverIP = properties.getProperty("server-ip");
                this.serverPort = Integer.valueOf(properties.getProperty("server-port"));
                this.onlineMode = Boolean.valueOf(properties.getProperty("online-mode"));
                this.userName = properties.getProperty("account-username");
                this.password = properties.getProperty("account-password");
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                FishingBot.getLog().severe("The given port is out of range!");
            }
        }
    }
}
