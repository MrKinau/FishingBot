package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandBye extends BrigardierCommand {

    public CommandBye() {
        super("bye", FishingBot.getI18n().t("command-bye-desc"), "stop", "shutdown");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.executes(context -> {
            CommandExecutor source = context.getSource();

            source.sendTranslatedMessages("command-bye");

            FishingBot.getInstance().stopBot(true);
            return 0;
        });
    }
}
