package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class ByeCommand extends Command {

    public ByeCommand() {
        super("bye", "let me disconnect and stop", "stop", "shutdown");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage("Goodbye.", executor);

        //TODO: cleaner solution
        System.exit(0);
    }
}
