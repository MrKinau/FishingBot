/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io;

import lombok.Getter;
import lombok.ToString;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.i18n.Language;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
    @Property(key = "auto.auto-sneak") private boolean autoSneak = false;
    @Property(key = "auto.auto-disconnect-players-threshold") private int autoDisconnectPlayersThreshold = 5;
    @Property(key = "auto.auto-command-on-respawn.enabled") private boolean autoCommandOnRespawnEnabled = false;
    @Property(key = "auto.auto-command-on-respawn.delay") private long autoCommandOnRespawnDelay = 1000;
    @Property(key = "auto.auto-command-on-respawn.commands") private List<String> autoCommandOnRespawn = Arrays.asList("%prefix%I respawned", "/home fishing");
    @Property(key = "auto.auto-command-before-death.commands") private List<String> autoCommandBeforeDeath = Arrays.asList("%prefix%I am about to die", "/home");
    @Property(key = "auto.auto-command-before-death.enabled") private boolean autoCommandBeforeDeathEnabled = false;
    @Property(key = "auto.auto-command-before-death.min-health-before-death") private float minHealthBeforeDeath = 6.0F;
    @Property(key = "auto.auto-quit-before-death.enabled") private boolean autoQuitBeforeDeathEnabled = false;
    @Property(key = "auto.auto-quit-before-death.min-health-before-quit") private float minHealthBeforeQuit = 6.0F;

    @Property(key = "account.mail") private String userName = "my-minecraft@login.com";
    @Property(key = "account.password") private String password = "CHANGEME";

    @Property(key = "logs.log-count") private int logCount = 15;
    @Property(key = "logs.log-packets") private boolean logPackets = false;

    @Property(key = "announces.discord.enabled") private boolean webHookEnabled = false;
    @Property(key = "announces.discord.web-hook") private String webHook = "YOURWEBHOOK";
    @Property(key = "announces.discord.announce-type-discord") private AnnounceType announceTypeDiscord = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announces.discord.alert-on-attack") private boolean alertOnAttack = true;
    @Property(key = "announces.discord.alert-on-respawn") private boolean alertOnRespawn = true;
    @Property(key = "announces.discord.alert-on-level-update") private boolean alertOnLevelUpdate = true;
    @Property(key = "announces.discord.ping-on-enchantment.enabled") private boolean pingOnEnchantmentEnabled = false;
    @Property(key = "announces.discord.ping-on-enchantment.mention") private String pingOnEnchantmentMention = "<@USER_ID>";
    @Property(key = "announces.discord.ping-on-enchantment.items") private List<String> pingOnEnchantmentItems = Collections.singletonList("enchanted_book");
    @Property(key = "announces.discord.ping-on-enchantment.enchantments") private List<String> pingOnEnchantmentEnchantments = Arrays.asList("MENDING","UNBREAKING");

    @Property(key = "announces.announce-type-chat") private AnnounceType announceTypeChat = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announces.announce-type-console") private AnnounceType announceTypeConsole = AnnounceType.ALL;
    @Property(key = "announces.announce-lvl-up") private boolean announceLvlUp = true;
    @Property(key = "announces.announce-lvl-up-text") private String announceLvlUpText = "I am level %lvl% now";

    @Property(key = "start-text.enabled") private boolean startTextEnabled = true;
    @Property(key = "start-text.text") private List<String> startText = Arrays.asList("%prefix%Starting fishing", "/trigger Bot");

    @Property(key = "misc.stucking-fix-enabled") private boolean stuckingFixEnabled = true;
    @Property(key = "misc.prevent-rod-breaking") private boolean preventRodBreaking = true;
    @Property(key = "misc.gui-console-max-lines") private int guiConsoleMaxLines = 1000;
    @Property(key = "misc.language") private Language language = Language.getByLocale(Locale.getDefault());
    @Property(key = "misc.wiki") private String readme = "https://github.com/MrKinau/FishingBot/wiki/config";

    @Getter private final String path;

    public SettingsConfig(String path) {
        this.path = path;
        init(path);
    }
}
