package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Displays this help message");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        String cmdPrefix = "/";
        if (executor == CommandExecutor.OTHER_PLAYER)
            cmdPrefix = "<My name>, ";

        String finalCmdPrefix = cmdPrefix;
        FishingBot.getInstance().getCommandRegistry().getRegisteredCommands().forEach(command -> {
            sendMessage(finalCmdPrefix + command.getLabel() + " - " + command.getDescription(), executor);
        });
    }
}
