package systems.kinau.fishingbot.modules.command.commands;

import javafx.application.Platform;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.utils.ItemUtils;
import systems.kinau.fishingbot.utils.LocationUtils;

public class EmptyCommand extends Command {

    public EmptyCommand() {
        super("empty", FishingBot.getI18n().t("command-empty-desc"), "clear", "drop");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        sendMessage(executor, "command-empty");

        if (args.length == 1) {
            LocationUtils.Direction direction;
            try {
                direction = LocationUtils.Direction.valueOf(args[0]);
            } catch (Exception ex) {
                sendMessage(executor, "command-empty-unknown-type", args[0].toUpperCase());
                return;
            }

            float yawBefore = FishingBot.getInstance().getCurrentBot().getPlayer().getYaw();
            float pitchBefore = FishingBot.getInstance().getCurrentBot().getPlayer().getPitch();
            FishingBot.getInstance().getCurrentBot().getPlayer().look(direction, finished -> {
                empty();
                FishingBot.getInstance().getCurrentBot().getPlayer().look(yawBefore, pitchBefore, 8);
            });
            return;
        }
        empty();

    }

    private void empty() {
        for (short slotId = 9; slotId <= 44; slotId++) {
            if (slotId == FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())
                continue;
            if (ItemUtils.isFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getInventory().getContent().get(slotId)))
                continue;
            FishingBot.getInstance().getCurrentBot().getPlayer().dropStack(slotId, (short) (slotId - 8));
        }
        if (FishingBot.getInstance().getMainGUI() != null) {
            Platform.runLater(() -> FishingBot.getInstance().getMainGUIController().deleteAllData(null));
        }
    }
}
