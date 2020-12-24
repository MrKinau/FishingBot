package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class LevelCommand extends Command {

    public LevelCommand() {
        super("level", FishingBot.getI18n().t("command-level-desc"), "level?");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage(executor, "command-level", FishingBot.getInstance().getCurrentBot().getPlayer().getLevels());
    }
}
