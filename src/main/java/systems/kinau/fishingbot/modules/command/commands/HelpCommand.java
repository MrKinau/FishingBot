package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", FishingBot.getI18n().t("command-help-desc"));
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        FishingBot.getInstance().getCurrentBot().getCommandRegistry().getRegisteredCommands().forEach(command -> {
            if (executor == CommandExecutor.OTHER_PLAYER)
                sendMessage(executor, "command-help-other-player", command.getLabel(), command.getDescription());
            else if (executor == CommandExecutor.CONSOLE)
                sendMessage(executor, "command-help-console", command.getLabel(), command.getDescription());
        });
    }
}
