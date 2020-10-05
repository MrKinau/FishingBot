/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io;

import lombok.Getter;
import lombok.ToString;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

@Getter
@ToString
public class SettingsConfig implements Config {

    @Property(key = "server.ip") private String serverIP = "127.0.0.1";
    @Property(key = "server.port") private int serverPort = 25565;
    @Property(key = "server.realm-id") private long realmId = -1;
    @Property(key = "server.realm-accept-tos") private boolean realmAcceptTos = false;
    @Property(key = "server.online-mode") private boolean onlineMode = true;
    @Property(key = "server.default-protocol") private String defaultProtocol = ProtocolConstants.getVersionString(ProtocolConstants.MINECRAFT_1_8);
    @Property(key = "server.spoof-forge") private boolean spoofForge = false;

    @Property(key = "auto.auto-reconnect") private boolean autoReconnect = true;
    @Property(key = "auto.auto-reconnect-time") private int autoReconnectTime = 5;
    @Property(key = "auto.auto-disconnect") private boolean autoDisconnect = false;
    @Property(key = "auto.auto-disconnect-players-threshold") private int autoDisconnectPlayersThreshold = 5;

    @Property(key = "account.mail") private String userName = "my-minecraft@login.com";
    @Property(key = "account.password") private String password = "CHANGEME";

    @Property(key = "logs.log-count") private int logCount = 15;
    @Property(key = "logs.log-packets") private boolean logPackets = false;

    @Property(key = "announces.announce-type-chat") private AnnounceType announceTypeChat = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announces.announce-type-console") private AnnounceType announceTypeConsole = AnnounceType.ALL;
    @Property(key = "announces.announce-lvl-up") private boolean announceLvlUp = true;
    @Property(key = "announces.announce-lvl-up-text") private String announceLvlUpText = "I am level %lvl% now";

    @Property(key = "start-text.enabled") private boolean startTextEnabled = true;
    @Property(key = "start-text.text") private String startText = "%prefix%Starting fishing;/trigger Bot";

    @Property(key = "discord.enabled") private boolean webHookEnabled = false;
    @Property(key = "discord.web-hook") private String webHook = "YOURWEBHOOK";

    @Property(key = "misc.stucking-fix-enabled") private boolean stuckingFixEnabled = true;
    @Property(key = "misc.prevent-rod-breaking") private boolean preventRodBreaking = true;
    @Property(key = "misc.wiki") private String readme = "https://github.com/MrKinau/FishingBot/wiki/config";

    @Getter private final String path;

    public SettingsConfig(String path) {
        this.path = path;
        init(path);
    }
}
