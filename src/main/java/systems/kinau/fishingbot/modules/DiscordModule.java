package systems.kinau.fishingbot.modules;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.event.custom.RespawnEvent;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.io.discord.DiscordDetails;
import systems.kinau.fishingbot.io.discord.DiscordMessageDispatcher;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DiscordModule extends Module implements Listener {

    private final DiscordDetails DISCORD_DETAILS = new DiscordDetails("FishingBot", "https://vignette.wikia.nocookie.net/mcmmo/images/2/2f/FishingRod.png");

    @Getter private DiscordMessageDispatcher discord;

    @Getter @Setter private float health = -1;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getEventManager().registerListener(this);
        //Activate Discord web hook
        if(FishingBot.getInstance().getConfig().isWebHookEnabled() && !FishingBot.getInstance().getConfig().getWebHook().equalsIgnoreCase("false") && !FishingBot.getInstance().getConfig().getWebHook().equals("YOURWEBHOOK"))
            this.discord = new DiscordMessageDispatcher(FishingBot.getInstance().getConfig().getWebHook());
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onCaught(FishCaughtEvent event) {
        if (getDiscord() != null) {
            FishingModule fishingModule = FishingBot.getInstance().getFishingModule();
            if (fishingModule == null)
                return;
            new Thread(() -> {
                String mention = "";
                if (FishingBot.getInstance().getConfig().isPingOnEnchantmentEnabled()) {
                    boolean itemMatches = FishingBot.getInstance().getConfig().getPingOnEnchantmentItems().isEmpty()
                            || FishingBot.getInstance().getConfig().getPingOnEnchantmentItems().contains(event.getItem().getName());
                    List<String> enchantmentFilter = FishingBot.getInstance().getConfig().getPingOnEnchantmentEnchantments();
                    boolean enchantmentMatches = FishingBot.getInstance().getConfig().getPingOnEnchantmentEnchantments().isEmpty()
                            || event.getItem().getEnchantments().stream()
                                    .anyMatch(enchantment -> enchantmentFilter.contains(enchantment.getEnchantmentType().getName().toUpperCase()));
                    if (itemMatches && enchantmentMatches) {
                        mention = FishingBot.getInstance().getConfig().getPingOnEnchantmentMention() + " ";
                    }
                }
                String finalMention = mention;
                fishingModule.logItem(
                        event.getItem(),
                        FishingBot.getInstance().getConfig().getAnnounceTypeDiscord(),
                        s -> getDiscord().dispatchMessage(finalMention + "`" + s + "`", DISCORD_DETAILS),
                        s -> {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getDiscord().dispatchMessage("`" + s + "`", DISCORD_DETAILS);
                        });
            }).start();
        }
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent event) {
        if (event.getEid() != FishingBot.getInstance().getPlayer().getEntityID())
            return;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
        numberFormat.setMaximumFractionDigits(2);
        String mention = FishingBot.getInstance().getConfig().getPingOnEnchantmentMention() + " ";
        if (mention.equals("<@USER_ID> "))
            mention = "";
        if (FishingBot.getInstance().getConfig().isAlertOnAttack() && getHealth() > event.getHealth())
            getDiscord().dispatchMessage(mention + "I got damage. My current life is " + numberFormat.format(event.getHealth()), DISCORD_DETAILS);
        this.health = event.getHealth();
    }

    @EventHandler
    public void onRespawn(RespawnEvent event) {
        if (FishingBot.getInstance().getConfig().isAlertOnRespawn()) {
            String mention = FishingBot.getInstance().getConfig().getPingOnEnchantmentMention() + " ";
            if (mention.equals("<@USER_ID> "))
                mention = "";
            getDiscord().dispatchMessage(mention + "I respawned", DISCORD_DETAILS);
        }
    }
}
