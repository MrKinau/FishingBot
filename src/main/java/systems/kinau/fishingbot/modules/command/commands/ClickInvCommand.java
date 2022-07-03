package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.network.protocol.play.PacketOutClickWindow;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClickInvCommand extends Command {

    public ClickInvCommand() {
        super("clickinv", FishingBot.getI18n().t("command-clickinv-desc"), "invclick");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        Optional<Integer> openedWindow = FishingBot.getInstance().getCurrentBot().getPlayer().getOpenedInventories().keySet().stream().max(Comparator.comparingInt(integer -> integer));
        if (!openedWindow.isPresent() || openedWindow.get() <= 0) {
            sendMessage(executor, "command-clickinv-no-inv");
            return;
        }

        try {
            if (args.length < 1)
                throw new IllegalArgumentException();

            byte button = args.length > 1 && args[1].equalsIgnoreCase("right") ? (byte)1 : 0;
            short slot = (short)(Short.parseShort(args[0]) - 1);

            Inventory inventory = FishingBot.getInstance().getCurrentBot().getPlayer().getOpenedInventories().get(openedWindow.get());
            if (!inventory.getContent().containsKey((int)slot)) {
                sendMessage(executor, "command-clickinv-invalid-slot");
                return;
            }

            Map<Short, Slot> remainingSlots = new HashMap<>();
            remainingSlots.put(slot, Slot.EMPTY);
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                    new PacketOutClickWindow(openedWindow.get(),
                            slot,
                            button,
                            inventory.getActionCounter(),
                            (short)0,
                            inventory.getContent().get((int)slot),
                            remainingSlots
                    )
            );
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IllegalArgumentException ex) {
            sendMessage("/clickinv <slot> [left|right]", executor);
        }
    }
}
