package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;
import systems.kinau.fishingbot.network.utils.ItemUtils;

public class EmptyCommand extends Command {

    public EmptyCommand() {
        super("empty", FishingBot.getI18n().t("command-empty-desc"), "clear", "drop");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage(executor, "command-empty");

        for (short slotId = 9; slotId <= 44; slotId++) {
            if (slotId == FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())
                continue;
            if (ItemUtils.isFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(slotId)))
                continue;
            FishingBot.getInstance().getCurrentBot().getPlayer().dropStack(slotId, (short) (slotId - 8));
        }
    }
}
