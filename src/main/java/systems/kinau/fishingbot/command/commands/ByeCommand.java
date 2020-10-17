package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class ByeCommand extends Command {

    public ByeCommand() {
        super("bye", FishingBot.getI18n().t("command-bye-desc"), "stop", "shutdown");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage(executor, "comand-bye");

        //TODO: cleaner solution
        System.exit(0);
    }
}
