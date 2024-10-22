package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandRightClick extends BrigardierCommand {

    public CommandRightClick() {
        super("rightclick", FishingBot.getI18n().t("command-rightclick-desc"), "use");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            CommandExecutor source = context.getSource();
                            int slot = context.getArgument("slot", Integer.class) - 1;
                            if (slot < 0 || slot > 8) {
                                source.sendTranslatedMessages("command-rightclick-invalid-slot", slot + 1);
                                return 0;
                            }
                            FishingBot.getInstance().getCurrentBot().getPlayer().setHeldSlot(slot + 36);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            FishingBot.getInstance().getCurrentBot().getPlayer().use();
                            return 0;
                        }))
                .executes(context -> {
                    FishingBot.getInstance().getCurrentBot().getPlayer().use();
                    return 0;
                });
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-rightclick-syntax", label);
    }
}
