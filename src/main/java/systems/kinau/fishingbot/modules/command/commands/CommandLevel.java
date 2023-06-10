package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandLevel extends BrigardierCommand {

    public CommandLevel() {
        super("level", FishingBot.getI18n().t("command-level-desc"), "level?");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.executes(context -> {
            context.getSource().sendTranslatedMessages("command-level", FishingBot.getInstance().getCurrentBot().getPlayer().getLevels());
            return 0;
        });
    }
}
