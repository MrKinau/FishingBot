package systems.kinau.fishingbot.modules.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.Getter;
import systems.kinau.fishingbot.modules.command.commands.CommandAcknowledgeCoC;
import systems.kinau.fishingbot.modules.command.commands.CommandBye;
import systems.kinau.fishingbot.modules.command.commands.CommandClickInv;
import systems.kinau.fishingbot.modules.command.commands.CommandDropRod;
import systems.kinau.fishingbot.modules.command.commands.CommandEmpty;
import systems.kinau.fishingbot.modules.command.commands.CommandHelp;
import systems.kinau.fishingbot.modules.command.commands.CommandLevel;
import systems.kinau.fishingbot.modules.command.commands.CommandLook;
import systems.kinau.fishingbot.modules.command.commands.CommandRightClick;
import systems.kinau.fishingbot.modules.command.commands.CommandStuck;
import systems.kinau.fishingbot.modules.command.commands.CommandSummary;
import systems.kinau.fishingbot.modules.command.commands.CommandSwap;
import systems.kinau.fishingbot.modules.command.commands.CommandWait;
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
        registerCommand(new CommandAcknowledgeCoC());
    }

    public void registerCommand(BrigardierCommand command) {
        if (commandDispatcher == null) return;
        LiteralArgumentBuilder<CommandExecutor> builder = LiteralArgumentBuilder.literal(command.getLabel());
        command.register(builder);
        commandDispatcher.register(builder);
        command.getAliases().forEach(alias -> {
            LiteralArgumentBuilder<CommandExecutor> aliasBuilder = LiteralArgumentBuilder.literal(alias);
            command.register(aliasBuilder);
            commandDispatcher.register(aliasBuilder);
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
