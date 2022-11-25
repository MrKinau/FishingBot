package systems.kinau.fishingbot.modules.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

import java.util.Arrays;
import java.util.List;

public abstract class BrigardierCommand {

    @Getter private final String label;
    @Getter private final List<String> aliases;
    @Getter private final String description;

    public BrigardierCommand(String label, String description, String... aliases) {
        this.label = label.toLowerCase().trim();
        this.description = description;
        for (int i = 0; i < aliases.length; i++)
            aliases[i] = aliases[i].toLowerCase().trim();
        this.aliases = Arrays.asList(aliases);
    }

    public abstract void register(LiteralArgumentBuilder<CommandExecutor> builder);

    public String getSyntax(CommandContext<CommandExecutor> context) {
        return getSyntax(context.getInput().split(" ")[0]);
    }

    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-syntax-no-args", label);
    }

    protected <T> RequiredArgumentBuilder<CommandExecutor, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected LiteralArgumentBuilder<CommandExecutor> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

}
