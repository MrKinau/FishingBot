package systems.kinau.fishingbot.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.Command;
import systems.kinau.fishingbot.command.CommandExecutor;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", FishingBot.getI18n().t("command-help-desc"));
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        FishingBot.getInstance().getCommandRegistry().getRegisteredCommands().forEach(command -> {
            sendMessage(executor, "command-help", command.getLabel(), command.getDescription());
        });
    }
}
