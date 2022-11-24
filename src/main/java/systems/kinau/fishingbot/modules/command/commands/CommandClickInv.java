package systems.kinau.fishingbot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.modules.command.BrigardierCommand;
import systems.kinau.fishingbot.modules.command.executor.CommandExecutor;
import systems.kinau.fishingbot.network.protocol.play.PacketOutClickWindow;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandClickInv extends BrigardierCommand {

    public CommandClickInv() {
        super("clickinv", FishingBot.getI18n().t("command-clickinv-desc"), "invclick");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer(1))
                        .then(argument("button", StringArgumentType.word())
                                .executes(getExecutor()))
                        .executes(getExecutor()))
                .executes(context -> {
                    context.getSource().sendMessage("/" + context.getInput() + " <slot> [left|right]");
                    return 0;
                });
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();

            Optional<Integer> openedWindow = FishingBot.getInstance().getCurrentBot().getPlayer().getOpenedInventories().keySet().stream().max(Comparator.comparingInt(integer -> integer));
            if (!openedWindow.isPresent() || openedWindow.get() <= 0) {
                source.sendTranslatedMessages("command-clickinv-no-inv");
                return 0;
            }

            String buttonStr = context.getArgument("button", String.class);
            byte button = buttonStr == null || !buttonStr.equals("right") ? (byte) 0 : 1;

            short slot = (short) (context.getArgument("slot", Integer.class) - 1);

            Inventory inventory = FishingBot.getInstance().getCurrentBot().getPlayer().getOpenedInventories().get(openedWindow.get());
            if (!inventory.getContent().containsKey((int) slot)) {
                source.sendTranslatedMessages("command-clickinv-invalid-slot");
                return 0;
            }

            Map<Short, Slot> remainingSlots = new HashMap<>();
            remainingSlots.put(slot, Slot.EMPTY);
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(
                    new PacketOutClickWindow(openedWindow.get(),
                            slot,
                            button,
                            inventory.getActionCounter(),
                            (short) 0,
                            inventory.getContent().get((int) slot),
                            remainingSlots
                    )
            );
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        };
    }
}
