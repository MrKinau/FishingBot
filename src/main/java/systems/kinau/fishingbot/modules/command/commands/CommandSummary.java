package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.loot.LootHistory;
import systems.kinau.fishingbot.bot.loot.LootItem;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

import java.util.Comparator;

public class CommandSummary extends BrigardierCommand {

    public CommandSummary() {
        super("summary", FishingBot.getI18n().t("command-summary-desc"), "summarize", "stats", "statistics", "caught", "loot");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(literal("clear")
                        .executes(getExecutor(true)))
                .executes(getExecutor(false));
    }

    private Command<CommandExecutor> getExecutor(boolean clearAfterwards) {
        return context -> {
            CommandExecutor source = context.getSource();
            if (FishingBot.getInstance().getCurrentBot() == null)
                return 0;
            if (FishingBot.getInstance().getCurrentBot().getFishingModule() == null)
                return 0;

            LootHistory lootHistory = FishingBot.getInstance().getCurrentBot().getFishingModule().getLootHistory();
            if (lootHistory.getItems().isEmpty()) {
                source.sendTranslatedMessages("command-summary-empty");
                return 0;
            }
            source.sendTranslatedMessages("ui-tabs-loot", lootHistory.getItems().stream().mapToInt(LootItem::getCount).sum());
            lootHistory.getItems().stream().sorted(Comparator.comparingInt(LootItem::getCount).reversed()).forEach(lootItem -> {
                source.sendMessage(lootItem.getCount() + "x " + lootItem.getDisplayName());
            });

            if (FishingBot.getInstance().getCurrentBot().getDiscordModule() != null)
                FishingBot.getInstance().getCurrentBot().getDiscordModule().sendSummary(lootHistory);

            if (clearAfterwards)
                lootHistory.getItems().clear();
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-summary-syntax", label);
    }
}
