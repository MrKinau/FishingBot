package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class StuckCommand extends Command {

    public StuckCommand() {
        super("stuck", "Casts the line again", "recast", "reeject", "refish", "recatch");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        FishingBot.getInstance().getFishingModule().stuck();
    }
}
