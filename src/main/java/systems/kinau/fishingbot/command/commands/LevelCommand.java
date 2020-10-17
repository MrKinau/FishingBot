package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class LevelCommand extends Command {

    private final String LEVEL_MSG = "I have %d levels";

    public LevelCommand() {
        super("level", FishingBot.getI18n().t("command-level-desc"), "level?");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage(executor, "command-level", FishingBot.getInstance().getPlayer().getLevels());
    }
}
