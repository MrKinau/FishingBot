package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.utils.ItemUtils;

/**
 * Created: 04.11.2020
 *
 * @author Summerfeeling
 */
public class DropRodCommand extends Command {

    public DropRodCommand() {
        super("droprod", FishingBot.getI18n().t("command-droprod-desc"), "roddrop", "droprods", "rodsdrop", "emptyrod", "rodempty");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        Filter filter = Filter.ALL_BUT_SELECTED;

        if (args.length >= 1) {
            try {
                filter = Filter.valueOf(String.join("_", args).toUpperCase());
            } catch (Exception e) {
                sendMessage(executor, "command-droprod-unknown-type", String.join("_", args).toUpperCase());
                return;
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

        sendMessage(executor, "command-droprod-item-count", dropCount);
    }

    public enum Filter {
        ALL,
        SELECTED,
        ALL_BUT_SELECTED
    }

}
