package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;
import systems.kinau.fishingbot.modules.fishing.FishingModule;

public class CommandStatus extends BrigardierCommand {

    public CommandStatus() {
        super("status", FishingBot.getI18n().t("command-status-desc"));
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.executes(context -> {
            CommandExecutor source = context.getSource();
            FishingModule fishingModule = FishingBot.getInstance().getCurrentBot() == null
                    ? null
                    : FishingBot.getInstance().getCurrentBot().getModuleManager().getModule(FishingModule.class);
            if (fishingModule == null) {
                source.sendTranslatedMessages("command-status-unavailable");
                return 0;
            }

            source.sendTranslatedMessages("command-status-result",
                    fishingModule.isPaused(),
                    fishingModule.isJoined(),
                    fishingModule.isNoRodAvailable(),
                    fishingModule.getCurrentBobber() != null,
                    // lastFish is the time of the last fishing action (cast in stuck()/run()/
                    // updateInventory(), reel-in in fish()), not the time a fish was caught.
                    // String.valueOf: MessageFormat would group the digits by locale.
                    String.valueOf(fishingModule.getLastFish()));
            return 0;
        });
    }
}
