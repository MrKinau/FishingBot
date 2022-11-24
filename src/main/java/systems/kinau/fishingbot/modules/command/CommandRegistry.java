package systems.kinau.fishingbot.modules.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.Getter;
import systems.kinau.fishingbot.modules.command.commands.CommandBye;
import systems.kinau.fishingbot.modules.command.commands.CommandClickInv;
import systems.kinau.fishingbot.modules.command.commands.CommandDropRod;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

public class CommandRegistry {

    @Getter private CommandDispatcher<CommandExecutor> commandDispatcher;

    public CommandRegistry() {
        RootCommandNode<CommandExecutor> rootNode = new RootCommandNode<>();
        this.commandDispatcher = new CommandDispatcher<>(rootNode);
    }

    public void registerBotCommands() {
        registerCommand(new CommandBye());
        registerCommand(new CommandClickInv());
        registerCommand(new CommandDropRod());
    }

    public void registerCommand(BrigardierCommand command) {
        if (commandDispatcher == null) return;
        LiteralArgumentBuilder<CommandExecutor> builder = LiteralArgumentBuilder.literal(command.getLabel());
        command.register(builder);
        commandDispatcher.register(builder);
        command.getAliases().forEach(alias -> {
            commandDispatcher.register(LiteralArgumentBuilder.<CommandExecutor>literal(alias)
                            .executes(commandDispatcher.getRoot().getChild(command.getLabel()).getCommand()));
        });
    }

    public boolean dispatchCommand(String cmdStr, CommandExecutor commandExecutor) {
        if (commandDispatcher == null) return false;
        if (cmdStr.startsWith("/"))
            cmdStr = cmdStr.substring(1);
        try {
            int result = commandDispatcher.execute(cmdStr, commandExecutor);
            if (result == 0)
                return true;
        } catch (CommandSyntaxException ignore) {}
        return false;
    }

}
