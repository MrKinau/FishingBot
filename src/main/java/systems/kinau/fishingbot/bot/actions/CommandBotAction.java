package systems.kinau.fishingbot.bot.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.Bot;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

@AllArgsConstructor
public class CommandBotAction extends BotAction {

    @Getter private String command;
    @Getter private boolean processAsBotCommand;

    @Override
    public boolean execute(Bot bot) {
        if (processAsBotCommand) {
            if (bot.getCommandRegistry().dispatchCommand(command, CommandExecutor.UNSET))
                return true;
        }
        bot.getPlayer().sendMessage(command);
        return true;
    }

}
