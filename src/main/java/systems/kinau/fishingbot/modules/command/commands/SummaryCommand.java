package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.loot.LootHistory;
import systems.kinau.fishingbot.bot.loot.LootItem;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

import java.util.Comparator;

public class SummaryCommand extends Command {

    public SummaryCommand() {
        super("summary", FishingBot.getI18n().t("command-summary-desc"), "summarize", "stats", "caught", "loot");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        if (FishingBot.getInstance().getCurrentBot() == null)
            return;
        if (FishingBot.getInstance().getCurrentBot().getFishingModule() == null)
            return;
        LootHistory lootHistory = FishingBot.getInstance().getCurrentBot().getFishingModule().getLootHistory();
        if (lootHistory.getItems().isEmpty()) {
            sendMessage(executor, "command-summary-empty");
            return;
        }
        sendMessage(executor, "ui-tabs-loot", lootHistory.getItems().stream().mapToInt(LootItem::getCount).sum());
        lootHistory.getItems().stream().sorted(Comparator.comparingInt(LootItem::getCount).reversed()).forEach(lootItem -> {
            sendMessage(lootItem.getCount() + "x " + lootItem.getName(), executor);
        });

        if (FishingBot.getInstance().getCurrentBot().getDiscordModule() != null)
            FishingBot.getInstance().getCurrentBot().getDiscordModule().sendSummary(lootHistory);
    }

}
