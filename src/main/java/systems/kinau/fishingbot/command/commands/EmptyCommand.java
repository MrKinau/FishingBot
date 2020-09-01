package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class EmptyCommand extends Command {

    public EmptyCommand() {
        super("empty", "Drops all contents of my inventory (except the fishing rod)", "clear", "drop");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage("Dropping inventory", executor);

        for (short slotId = 9; slotId <= 44; slotId++) {
            if (slotId == FishingBot.getInstance().getPlayer().getHeldSlot())
                continue;
            FishingBot.getInstance().getPlayer().dropStack(slotId, (short) (slotId - 8));
        }
    }
}
