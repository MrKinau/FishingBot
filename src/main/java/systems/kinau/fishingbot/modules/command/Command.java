package systems.kinau.fishingbot.modules.command;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;

import java.util.Arrays;
import java.util.List;

public abstract class Command {

    @Getter private final String label;
    @Getter private final List<String> aliases;
    @Getter private final String description;

    public Command(String label, String description, String... aliases) {
        this.label = label.toLowerCase().trim();
        this.description = description;
        for (int i = 0; i < aliases.length; i++)
            aliases[i] = aliases[i].toLowerCase().trim();
        this.aliases = Arrays.asList(aliases);
    }

    public abstract void onCommand(String label, String[] args, CommandExecutor executor);

    public void sendMessage(String message, CommandExecutor executor) {
        if (executor == CommandExecutor.CONSOLE)
            FishingBot.getLog().info(message);
        else
            FishingBot.getInstance().getCurrentBot().getPlayer().sendMessage(message);
    }

    public void sendMessage(CommandExecutor executor, String key, Object... args) {
        this.sendMessage(FishingBot.getI18n().t(key, args), executor);
    }

}
