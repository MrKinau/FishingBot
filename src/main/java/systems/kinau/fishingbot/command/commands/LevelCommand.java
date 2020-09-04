package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class LevelCommand extends Command {

    private final String LEVEL_MSG = "I have %d levels";

    public LevelCommand() {
        super("level", "Displays my current level", "level?");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        String msg = String.format(LEVEL_MSG, FishingBot.getInstance().getPlayer().getLevels());
        sendMessage(msg, executor);
    }
}
