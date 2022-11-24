package systems.kinau.fishingbot.modules.command.brigardier.node;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class LiteralNode extends Node {

    public LiteralNode(String name) {
        super(name);
    }

    @Override
    public ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder() {
        return LiteralArgumentBuilder.literal(name);
    }
}