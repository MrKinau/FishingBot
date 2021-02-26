package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class StuckCommand extends Command {

    public StuckCommand() {
        super("stuck", FishingBot.getI18n().t("command-stuck-desc"), "recast", "reeject", "refish", "recatch");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        FishingBot.getInstance().getCurrentBot().getFishingModule().stuck();
        sendMessage(executor, "command-stuck-executed");
    }
}
