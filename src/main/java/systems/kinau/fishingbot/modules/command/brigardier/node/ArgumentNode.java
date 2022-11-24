package systems.kinau.fishingbot.modules.command.brigardier.node;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Getter;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.modules.command.brigardier.argument.IdentifiableArgumentType;

@Getter
public class ArgumentNode<T extends com.mojang.brigadier.arguments.ArgumentType<?>> extends Node {

    private final IdentifiableArgumentType<T> argumentType;
    private final String identifier;

    public ArgumentNode(String name, IdentifiableArgumentType<T> argumentType, String identifier) {
        super(name);
        this.argumentType = argumentType;
        this.identifier = identifier;
    }

    @Override
    public ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder() {
        com.mojang.brigadier.arguments.ArgumentType<?> type = argumentType.createType();
        RequiredArgumentBuilder<CommandExecutor, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(name, type);
//            if (identifier != null)
//                requiredArgumentBuilder.suggests(SuggestionProviders.byId(this.id));
        return requiredArgumentBuilder;
    }
}