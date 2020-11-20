package systems.kinau.fishingbot.modules;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.event.custom.RespawnEvent;
import systems.kinau.fishingbot.event.play.UpdateExperienceEvent;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.fishing.ItemHandler;
import systems.kinau.fishingbot.io.discord.DiscordDetails;
import systems.kinau.fishingbot.io.discord.DiscordMessageDispatcher;

import java.text.NumberFormat;
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
//                getDiscord().dispatchMessage(mention, DISCORD_DETAILS);
                fishingModule.logItem(
                        event.getItem(),
                        FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeDiscord(),
                        s -> getDiscord().dispatchEmbed(event.getItem().getName(), 0x00ff00,
                                ItemHandler.getImageUrl(event.getItem().getName()), event.getItem().getEnchantments().toString(),
                                "Fishing Rod Durability: 1/1 o " + FishingBot.getInstance().getCurrentBot().getAuthData().getUsername(), DISCORD_DETAILS),
                        s -> {
                            getDiscord().dispatchEmbed(event.getItem().getName(), 0x00ff00,
                                    ItemHandler.getImageUrl(event.getItem().getName()), event.getItem().getEnchantments().toString(),
                                    "Fishing Rod Durability: 1/1 o " + FishingBot.getInstance().getCurrentBot().getAuthData().getUsername(), DISCORD_DETAILS);
                        });
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
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAlertOnAttack() && getHealth() > event.getHealth())
            getDiscord().dispatchMessage(mention + FishingBot.getI18n().t("discord-webhook-damage", numberFormat.format(event.getHealth())), DISCORD_DETAILS);
        this.health = event.getHealth();
    }

    @EventHandler
    public void onXP(UpdateExperienceEvent event) {
        if (getLevel() != event.getLevel() && FishingBot.getInstance().getCurrentBot().getConfig().isAlertOnLevelUpdate())
            getDiscord().dispatchMessage(FishingBot.getI18n().t("announce-level-up", String.valueOf(event.getLevel())), DISCORD_DETAILS);
        this.level = event.getLevel();
    }

    @EventHandler
    public void onRespawn(RespawnEvent event) {
        if (FishingBot.getInstance().getCurrentBot().getConfig().isAlertOnRespawn()) {
            String mention = FishingBot.getInstance().getCurrentBot().getConfig().getPingOnEnchantmentMention() + " ";
            if (mention.equals("<@USER_ID> "))
                mention = "";

            getDiscord().dispatchMessage(mention + FishingBot.getI18n().t("discord-webhook-respawn"), DISCORD_DETAILS);
        }
    }
}
