package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandSwap extends BrigardierCommand {

    public CommandSwap() {
        super("swap", FishingBot.getI18n().t("command-swap-desc"), "swapitem");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer())
                        .then(argument("hotbar", IntegerArgumentType.integer(1, 9))
                                .executes(context -> {
                                    Player player = FishingBot.getInstance().getCurrentBot().getPlayer();

                                    int slot1 = context.getArgument("slot", Integer.class);
                                    int slot2 = context.getArgument("hotbar", Integer.class);
                                    player.swapToHotBar(slot1, slot2);
                                    return 0;
                                }))
                        .executes(context -> {
                            context.getSource().sendMessage(getSyntax(context));
                            return 0;
                        }))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-swap-syntax", label);
    }
}
