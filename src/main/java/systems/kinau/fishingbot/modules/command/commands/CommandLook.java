package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandLook extends BrigardierCommand {

    public CommandLook() {
        super("look", FishingBot.getI18n().t("command-look-desc"));
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("yaw", FloatArgumentType.floatArg(-180.0f, 180.0f))
                        .then(argument("pitch", FloatArgumentType.floatArg(-180.0f, 180.0f))
                                .then(argument("speed", IntegerArgumentType.integer(1))
                                        .executes(getExecutor()))
                                .executes(getExecutor()))
                        .executes(context -> {
                            context.getSource().sendMessage(getSyntax(context));
                            return 0;
                        }))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();
            float yaw = context.getArgument("yaw", Float.class);
            float pitch = context.getArgument("pitch", Float.class);
            int speed = FishingBot.getInstance().getCurrentBot().getConfig().getLookSpeed();
            Integer speedInput = context.getArgument("speed", Integer.class);
            if (speedInput != null)
                speed = speedInput;
            FishingBot.getInstance().getCurrentBot().getPlayer().look(yaw, pitch, speed, finished -> {
                source.sendTranslatedMessages("command-look-executed", yaw, pitch);
            });
            FishingBot.getInstance().getCurrentBot().getPlayer().setOriginYaw(yaw);
            FishingBot.getInstance().getCurrentBot().getPlayer().setOriginPitch(pitch);
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-look-syntax", label);
    }
}
