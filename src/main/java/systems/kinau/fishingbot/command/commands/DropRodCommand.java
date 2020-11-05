package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;
import systems.kinau.fishingbot.network.utils.ItemUtils;

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
        Filter filter = Filter.DROP_ALL_BUT_SELECTED;

        if (args.length == 1) {
            try {
                filter = Filter.valueOf(args[0].toUpperCase());
            } catch (Exception e) {
                sendMessage(executor, "command-droprod-unknown-type", args[0]);
                return;
            }
        }

        Inventory inventory = FishingBot.getInstance().getPlayer().getInventory();
        int dropCount = 0;

        for (int slotId : inventory.getContent().keySet()) {
            Slot slot = inventory.getContent().get(slotId);
            if (!ItemUtils.isFishingRod(slot)) continue;

            if (filter == Filter.DROP_ALL || (filter == Filter.DROP_ALL_BUT_SELECTED && slotId != FishingBot.getInstance().getPlayer().getHeldSlot())) {
                FishingBot.getInstance().getPlayer().dropStack((short) slotId, (short) (slotId - 8));
                dropCount++;
            } else if (filter == Filter.DROP_SELECTED && slotId == FishingBot.getInstance().getPlayer().getHeldSlot()) {
                FishingBot.getInstance().getPlayer().dropStack((short) slotId, (short) (slotId - 8));
                dropCount++;
                break;
            }
        }

        sendMessage(executor, "command-droprod-item-count", dropCount);
    }

    public enum Filter {
        DROP_ALL,
        DROP_SELECTED,
        DROP_ALL_BUT_SELECTED
    }

}
