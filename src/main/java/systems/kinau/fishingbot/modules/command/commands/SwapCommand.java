package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class SwapCommand extends Command {

    public SwapCommand() {
        super("swap", FishingBot.getI18n().t("command-swap-desc"), "swapitem");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        Player player = FishingBot.getInstance().getCurrentBot().getPlayer();

        if (args.length == 2) {
            try {
                int slot1 = Integer.parseInt(args[0]);
                int slot2 = Integer.parseInt(args[1]);
                player.swapToHotBar(slot1, slot2);
            } catch (NumberFormatException ex) {
                sendMessage(executor, "command-swap-syntax");
            }
        } else {
            sendMessage(executor, "command-swap-syntax");
        }
    }
}
