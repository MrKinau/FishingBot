package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;
import systems.kinau.fishingbot.utils.ItemUtils;

public class CommandDropRod extends BrigardierCommand {

    public CommandDropRod() {
        super("droprod", FishingBot.getI18n().t("command-droprod-desc"), "roddrop", "droprods", "rodsdrop", "emptyrod", "rodempty");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("filter", StringArgumentType.greedyString())
                        .executes(getExecutor()))
                .executes(getExecutor());
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();
            Filter filter = Filter.ALL_BUT_SELECTED;

            String filterStr = null;
            try {
                filterStr = context.getArgument("filter", String.class);
            } catch (IllegalArgumentException ignore) {}

            if (filterStr != null) {
                try {
                    filter = Filter.valueOf(filterStr.replace(" ", "_").toUpperCase());
                } catch (Exception e) {
                    source.sendTranslatedMessages("command-droprod-unknown-type", filterStr.replace(" ", "_").toUpperCase());
                    return 0;
                }
            }

            Inventory inventory = FishingBot.getInstance().getCurrentBot().getPlayer().getInventory();
            int dropCount = 0;

            for (int slotId : inventory.getContent().keySet()) {
                Slot slot = inventory.getContent().get(slotId);
                if (!ItemUtils.isFishingRod(slot)) continue;

                if (filter == Filter.ALL || (filter == Filter.ALL_BUT_SELECTED && slotId != FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())) {
                    FishingBot.getInstance().getCurrentBot().getPlayer().dropStack((short) slotId, (short) (slotId - 8));
                    dropCount++;
                } else if (filter == Filter.SELECTED && slotId == FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot()) {
                    FishingBot.getInstance().getCurrentBot().getPlayer().dropStack((short) slotId, (short) (slotId - 8));
                    FishingBot.getInstance().getCurrentBot().getFishingModule().swapWithBestFishingRod();
                    dropCount++;
                    break;
                }
            }

            source.sendTranslatedMessages("command-droprod-item-count", dropCount);
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-droprod-syntax", label);
    }

    public enum Filter {
        ALL,
        SELECTED,
        ALL_BUT_SELECTED
    }

}
