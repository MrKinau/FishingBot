package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class StuckCommand extends Command {

    public StuckCommand() {
        super("stuck", FishingBot.getI18n().t("command-stuck-help"), "recast", "reeject", "refish", "recatch");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        FishingBot.getInstance().getCurrentBot().getFishingModule().stuck();
    }
}
