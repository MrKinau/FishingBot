package systems.kinau.fishingbot.modules.command.brigardier.node;

import com.mojang.brigadier.builder.ArgumentBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

@AllArgsConstructor
@Getter
public abstract class Node {
    protected final String name;

    public abstract ArgumentBuilder<CommandExecutor, ?> createArgumentBuilder();
}