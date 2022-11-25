package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandWait extends BrigardierCommand {

    public CommandWait() {
        super("wait", FishingBot.getI18n().t("command-wait-desc"), "sleep");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("time", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            int time = context.getArgument("time", Integer.class);

                            try {
                                Thread.sleep(time * 1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            context.getSource().sendTranslatedMessages("command-wait-waited", time);
                            return 0;
                        }))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-wait-syntax", label);
    }
}
