package systems.kinau.fishingbot.modules.command.executor;

import systems.kinau.fishingbot.FishingBot;

public class ConsoleCommandExecutor implements CommandExecutor {

    @Override
    public CommandExecutionType getType() {
        return CommandExecutionType.CONSOLE;
    }

    @Override
    public void sendMessage(String message) {
        FishingBot.getLog().info(message);
    }
}
