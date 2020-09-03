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
    @Property(key = "realm-id") private long realmId = -1;
    @Property(key = "realm-accept-tos") private boolean realmAcceptTos = false;
    @Property(key = "auto-reconnect") private boolean autoReconnect = true;
    @Property(key = "auto-reconnect-time") private int autoReconnectTime = 3;

    @Property(key = "online-mode") private boolean onlineMode = true;

    @Property(key = "account-username") private String userName = "FishingBot";
    @Property(key = "account-password") private String password = "CHANGEME";

    @Property(key = "log-count") private int logCount = 15;
    @Property(key = "log-packets") private boolean logPackets = false;
    @Property(key = "announce-type-chat") private AnnounceType announceTypeChat = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announce-type-console") private AnnounceType announceTypeConsole = AnnounceType.ALL;
    @Property(key = "announce-lvl-up") private String announceLvlUp = "I've got a new level: %lvl%";
    @Property(key = "start-text-enabled") private boolean startTextEnabled = true;
    @Property(key = "start-text") private String startText = "%prefix%Starting fishing;/trigger Bot";

    @Property(key = "default-protocol") private String defaultProtocol = ProtocolConstants.getVersionString(ProtocolConstants.MINECRAFT_1_8);

    @Property(key = "discord-webHook") private String webHook = "false";

    @Property(key = "auto-disconnect") private boolean autoDisconnect = false;
    @Property(key = "auto-disconnect-players-threshold") private int autoDisconnectPlayersThreshold = 5;

    @Property(key = "stucking-fix-enabled") private boolean stuckingFixEnabled = true;

    @Getter private String path;

    public SettingsConfig(String path) {
        String comments = "server-ip:\tServer IP the bot connects to\n" +
                "#server-port:\tPort of the server the bot connects to\n" +
                "#realm-id:\tID of the realm the bot should conmnect to (if this option is enabled the bot ignores the server-ip and server-port). To get the correct id start the bot in online-mode and set the realm-id to 0. At startup you will get a list of your connectable realms with the corresponding id.\n" +
                "#auto-reconnect:\tAuto-Reconnect if bot get kicked/time out etc\n" +
                "#auto-reconnect-time:\tThe time (in seconds) the bot waits after kick to reconnect (only usable if auto-reconnect is set to true)\n" +
                "#online-mode:\tToggles online-mode\n" +
                "#log-count:\t\t\t\tThe number of logs the bot generate\n" +
                "#announce-type-chat:\tThe type of chat announcement:\n" +
                "#announce-type-console:\tThe type of console log announcement:\n" +
                "# Announcement levels:\n"+
                "#\tALL:\tAnnounces everything caught\n" +
                "#\tALL_BUT_FISH:\tAnnounces everything excepts fish\n" +
                "#\tONLY_ENCHANTED:\tAnnounces only enchanted stuff\n" +
                "#\tONLY_BOOKS:\tAnnounces only enchanted books\n" +
                "#\tNONE:\tAnnounces nothing\n" +
                "#announce-lvl-up:\tText of the level-announcement in chat. %lvl% will be replaced with the gained level. Use \"false\" if you don't want to announce the achieved levels\n" +
                "#discord-webHook:\tUse this to send all chat messages from the bot to a Discord webhook\n" +
                "#start-text-enabled:\tIf disabled, the start-text will not be displayed\n" +
                "#start-text:\tChat messages/commands separated with a semicolon\n" +
                "#auto-disconnect:\tThe bot automatically disconnects (and cant connect) if a defined amount of players is reached\n" +
                "#auto-disconnect-players-threshold:\tIf this amount of players is reached and auto-disconnect is activated the bot cant connect or will be kicked\n" +
                "#account-username:\tThe username / e-mail of the account\n" +
                "#account-password:\tThe password of the account (ignored in offline-mode)\n" +
                "#default-protocol:\tOnly needed for Multi-Version servers. The Minecraft-Version for the ping request to the server. Possible values: (1.8, 1.9, 1.9.2, 1.9.2, 1.9.4, ...)\n" +
                "#stucking-fix-enabled:\tIf you dont want the bot to re-throw if no fish is caught after 60 seconds (Disabling may cause the bot stuck)";

        this.path = path;
        init(path, comments);
    }
}
