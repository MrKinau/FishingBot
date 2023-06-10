package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import javafx.application.Platform;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;
import systems.kinau.fishingbot.utils.ItemUtils;
import systems.kinau.fishingbot.utils.LocationUtils;

public class CommandEmpty extends BrigardierCommand {

    public CommandEmpty() {
        super("empty", FishingBot.getI18n().t("command-empty-desc"), "clear", "drop");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("direction", StringArgumentType.word())
                        .then(argument("slot", IntegerArgumentType.integer())
                                .executes(getExecutor()))
                        .executes(getExecutor()))
                .executes(getExecutor());
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();
            source.sendTranslatedMessages("command-empty");

            String directionStr = null;
            try {
                directionStr = context.getArgument("direction", String.class);
            } catch (IllegalArgumentException ignore) {}
            Integer slot = null;
            try {
                slot = context.getArgument("slot", Integer.class);
            } catch (IllegalArgumentException ignore) {}

            if (directionStr != null) {
                LocationUtils.Direction direction;
                try {
                    direction = LocationUtils.Direction.valueOf(directionStr);
                } catch (Exception ex) {
                    source.sendTranslatedMessages("command-empty-unknown-type", directionStr.toUpperCase());
                    return 0;
                }

                float yawBefore = FishingBot.getInstance().getCurrentBot().getPlayer().getYaw();
                float pitchBefore = FishingBot.getInstance().getCurrentBot().getPlayer().getPitch();
                Integer finalSlot = slot;
                FishingBot.getInstance().getCurrentBot().getPlayer().look(direction, finished -> {
                    if (finalSlot != null) {
                        drop(finalSlot.shortValue());
                    } else {
                        empty();
                    }
                    FishingBot.getInstance().getCurrentBot().getPlayer().look(yawBefore, pitchBefore, 8);
                });
                return 0;
            }
            empty();
            return 0;
        };
    }

    private void empty() {
        for (short slotId = 9; slotId <= 44; slotId++) {
            drop(slotId);
        }
        if (FishingBot.getInstance().getMainGUI() != null) {
            Platform.runLater(() -> FishingBot.getInstance().getMainGUIController().deleteAllData(null));
        }
    }

    private void drop(short slotId) {
        if (slotId == FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())
            return;
        if (ItemUtils.isFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(slotId)))
            return;
        FishingBot.getInstance().getCurrentBot().getPlayer().dropStack(slotId, (short) (slotId - 8));
    }

    @Override
    public String getSyntax(String label) {
        return FishingBot.getI18n().t("command-empty-syntax", label);
    }
}
