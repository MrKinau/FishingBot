package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class ByeCommand extends Command {

    public ByeCommand() {
        super("bye", FishingBot.getI18n().t("command-bye-desc"), "stop", "shutdown");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage(executor, "command-bye");

        FishingBot.getInstance().getCurrentBot().setPreventReconnect(true);
        FishingBot.getInstance().getCurrentBot().setRunning(false);
    }
}
