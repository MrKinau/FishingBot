/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io;

import lombok.Getter;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

@Getter
public class SettingsConfig implements Config {

    @Property(key = "server-ip") private String serverIP = "127.0.0.1";
    @Property(key = "server-port") private int serverPort = 25565;

    @Property(key = "online-mode") private boolean onlineMode = true;

    @Property(key = "account-username") private String userName = "FishingBot";
    @Property(key = "account-password") private String password = "CHANGEME";

    @Property(key = "log-count") private int logCount = 15;
    @Property(key = "announce-type-chat") private AnnounceType announceTypeChat = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announce-type-console") private AnnounceType announceTypeConsole = AnnounceType.ALL;
    @Property(key = "announce-lvl-up") private String announceLvlUp = "I've got a new level: %lvl%";
    @Property(key = "start-text") private String startText = "%prefix%Starting fishing;/trigger Bot";
    @Property(key = "proxy-chat") private boolean proxyChat = false;

    @Property(key = "default-protocol") private String defaultProtocol = ProtocolConstants.getVersionString(ProtocolConstants.MINECRAFT_1_8);

    @Property(key = "discord-webHook") private String webHook = "false";

    public SettingsConfig() {
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

        init("config.properties", comments);
    }
}
