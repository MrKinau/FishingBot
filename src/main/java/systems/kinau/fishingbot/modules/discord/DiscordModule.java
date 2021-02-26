package systems.kinau.fishingbot.modules.discord;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.loot.LootHistory;
import systems.kinau.fishingbot.bot.loot.LootItem;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.BotStopEvent;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.event.custom.RespawnEvent;
import systems.kinau.fishingbot.event.play.UpdateExperienceEvent;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.modules.Module;
import systems.kinau.fishingbot.modules.fishing.FishingModule;
import systems.kinau.fishingbot.modules.fishing.ItemHandler;
import systems.kinau.fishingbot.utils.StringUtils;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DiscordModule extends Module implements Listener {

    private final DiscordDetails DISCORD_DETAILS = new DiscordDetails("FishingBot", "https://vignette.wikia.nocookie.net/mcmmo/images/2/2f/FishingRod.png");

    @Getter private DiscordMessageDispatcher discord;

    @Getter @Setter private float health = -1;
    @Getter @Setter private int level = -1;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        //Activate Discord web hook
        if(FishingBot.getInstance().getCurrentBot().getConfig().isWebHookEnabled() && !FishingBot.getInstance().getCurrentBot().getConfig().getWebHook().equalsIgnoreCase("false")
                && !FishingBot.getInstance().getCurrentBot().getConfig().getWebHook().equals("YOURWEBHOOK"))
            this.discord = new DiscordMessageDispatcher(FishingBot.getInstance().getCurrentBot().getConfig().getWebHook());
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
        if (this.discord != null)
            this.discord.shutdown();
        this.discord = null;
    }

    public void sendSummary(LootHistory lootHistory) {
        if (!FishingBot.getInstance().getCurrentBot().getConfig().isWebHookEnabled())
            return;
        StringBuilder lootStr = new StringBuilder();
        lootHistory.getItems().stream().sorted(Comparator.comparingInt(LootItem::getCount).reversed())
                .forEach(lootItem -> lootStr.append(lootItem.getCount()).append("x ").append(lootItem.getName()).append("\n"));
        getDiscord().dispatchEmbed(FishingBot.getI18n().t("ui-tabs-loot", lootHistory.getItems().stream().mapToInt(LootItem::getCount).sum()), 0xff0000,
                "https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/items/fishing_rod.png",
                lootStr.toString(),
                getFooter(), DISCORD_DETAILS);
    }

    private String formatEnchantment(List<Enchantment> enchantments) {
        if (enchantments == null || enchantments.isEmpty())
            return null;
        StringBuilder sb = new StringBuilder("**Enchantments:**\n");
        enchantments.forEach(enchantment -> {
            sb.append(enchantment.getEnchantmentType().getName().toUpperCase());
            if (enchantment.getLevel() > 1)
                sb.append(" ").append(StringUtils.getRomanLevel(enchantment.getLevel()));
            sb.append("\n");
        });
        return sb.toString();
    }

    private int getColor(Item item) {
        if (item.getEnchantments() != null && !item.getEnchantments().isEmpty())
            return 0x6a01fb;
        switch (item.getName()) {
            case "nametag": return 0xf5e4bc;
            case "leather":
            case "saddle": return 0xda652a;
            case "bowl":
            case "stick":
            case "fishing_rod":
            case "leather_boots":
            case "rotten_flesh":
            case "tripwire_hook":
            case "bow": return 0x70402e;
            case "string":
            case "bone": return 0xe0e0e2;
            case "ink_sac": return 0x2c2c2d;
            case "lily_pad": return 0x18661f;
            case "bamboo": return 0x21a021;
            default: return 0x3ac8e7;
        }
    }

    private String getFooter() {
        int durability = 0;
        if (FishingBot.getInstance().getCurrentBot() == null)
            return "";
        if (FishingBot.getInstance().getCurrentBot().getPlayer() != null
                && FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem() != null)
            durability = Math.min(64 - FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem().getItemDamage(), 64);
        return "Fishing Rod Durability: " + durability + "/64  ●  " + FishingBot.getInstance().getCurrentBot().getAuthData().getUsername();
    }

    @EventHandler
    public void onCaught(FishCaughtEvent event) {
        if (getDiscord() != null) {
            FishingModule fishingModule = FishingBot.getInstance().getCurrentBot().getFishingModule();
            if (fishingModule == null)
                return;
            new Thread(() -> {
                String mention = "";
                if (FishingBot.getInstance().getCurrentBot().getConfig().isPingOnEnchantmentEnabled()) {
                    boolean itemMatches = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentItems().isEmpty()
                            || FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentItems().contains(event.getItem().getName());
                    List<String> enchantmentFilter = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentEnchantments();
                    boolean enchantmentMatches = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentEnchantments().isEmpty()
                            || event.getItem().getEnchantments().stream()
                                    .anyMatch(enchantment -> enchantmentFilter.contains(enchantment.getEnchantmentType().getName().toUpperCase()));
                    if (itemMatches && enchantmentMatches) {
                        mention = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentMention() + " ";
                    }
                }
                if (!mention.isEmpty())
                    getDiscord().dispatchMessage(mention, DISCORD_DETAILS);

                String itemName = event.getItem().getName().replace("_", " ").toLowerCase();
                StringBuilder sb = new StringBuilder();
                for (String s : itemName.split(" ")) {
                    s = s.substring(0, 1).toUpperCase() + s.substring(1);
                    sb.append(s).append(" ");
                }
                String finalItemName = sb.toString().trim();

                fishingModule.logItem(
                        event.getItem(),
                        FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeDiscord(),
                        s -> getDiscord().dispatchEmbed("**" + finalItemName + "**", getColor(event.getItem()),
                                ItemHandler.getImageUrl(event.getItem()), formatEnchantment(event.getItem().getEnchantments()),
                                getFooter(), DISCORD_DETAILS),
                        s -> { });
            }).start();
        }
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent event) {
        if (event.getEid() != FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID())
            return;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
        numberFormat.setMaximumFractionDigits(2);
        String mention = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentMention() + " ";
        if (mention.equals("<@USER_ID> "))
            mention = "";
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAlertOnAttack() && getHealth() > event.getHealth()) {
            if (!mention.isEmpty())
                getDiscord().dispatchMessage(mention, DISCORD_DETAILS);
            getDiscord().dispatchEmbed(FishingBot.getI18n().t("config-announces-discord-alert-on-attack"), 0xff0000,
                    "https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/general/heart.png",
                    FishingBot.getI18n().t("discord-webhook-damage", numberFormat.format(event.getHealth())),
                    getFooter(), DISCORD_DETAILS);
        }
        this.health = event.getHealth();
    }

    @EventHandler
    public void onXP(UpdateExperienceEvent event) {
        if (getLevel() != event.getLevel() && FishingBot.getInstance().getCurrentBot().getConfig().isAlertOnLevelUpdate()) {
            getDiscord().dispatchEmbed(FishingBot.getI18n().t("config-announces-discord-alert-on-level-update"), 0xb5ea3a,
                    "https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/general/xp.png",
                    FishingBot.getI18n().t("announce-level-up", String.valueOf(event.getLevel())),
                    getFooter(), DISCORD_DETAILS);
        }
        this.level = event.getLevel();
    }

    @EventHandler
    public void onRespawn(RespawnEvent event) {
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAlertOnRespawn()) {
            String mention = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentMention() + " ";
            if (mention.equals("<@USER_ID> "))
                mention = "";
            if (!mention.isEmpty())
                getDiscord().dispatchMessage(mention, DISCORD_DETAILS);
            getDiscord().dispatchEmbed(FishingBot.getI18n().t("config-announces-discord-alert-on-respawn"), 0x666666,
                    "https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/general/heart.png",
                    FishingBot.getI18n().t("discord-webhook-respawn"),
                    getFooter(), DISCORD_DETAILS);
        }
    }

    @EventHandler
    public void onBotStop(BotStopEvent event) {
        sendSummary(FishingBot.getInstance().getCurrentBot().getFishingModule().getLootHistory());
    }
}
