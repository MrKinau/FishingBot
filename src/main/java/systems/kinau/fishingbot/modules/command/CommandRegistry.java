package systems.kinau.fishingbot.modules.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.Getter;
import systems.kinau.fishingbot.modules.command.commands.*;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandRegistry {

    @Getter private CommandDispatcher<CommandExecutor> commandDispatcher;
    @Getter private List<BrigardierCommand> commands;

    public CommandRegistry() {
        RootCommandNode<CommandExecutor> rootNode = new RootCommandNode<>();
        this.commandDispatcher = new CommandDispatcher<>(rootNode);
        this.commands = new ArrayList<>();
    }

    public void registerBotCommands() {
        registerCommand(new CommandBye());
        registerCommand(new CommandClickInv());
        registerCommand(new CommandDropRod());
        registerCommand(new CommandEmpty());
        registerCommand(new CommandHelp());
        registerCommand(new CommandLevel());
        registerCommand(new CommandLook());
        registerCommand(new CommandRightClick());
        registerCommand(new CommandStuck());
        registerCommand(new CommandSummary());
        registerCommand(new CommandSwap());
        registerCommand(new CommandWait());
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
        commands.add(command);
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

    public Optional<BrigardierCommand> getCommand(String command) {
        return commands.stream().filter(cmd -> cmd.getLabel().equalsIgnoreCase(command) || cmd.getAliases().contains(command)).findAny();
    }
}
