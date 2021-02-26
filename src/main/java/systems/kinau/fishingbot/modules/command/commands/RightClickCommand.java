package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class RightClickCommand extends Command {

    public RightClickCommand() {
        super("rightclick", FishingBot.getI18n().t("command-rightclick-desc"), "use");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        if (args.length == 1) {
            try {
                int slot = Integer.parseInt(args[0]) - 1;
                if (slot < 0 || slot > 8) {
                    sendMessage(executor, "command-rightclick-invalid-slot", slot + 1);
                    return;
                }
                FishingBot.getInstance().getCurrentBot().getPlayer().setHeldSlot(slot);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                sendMessage("/rightclick [slot (1-9)]", executor);
            }
        }
        FishingBot.getInstance().getCurrentBot().getPlayer().use();
    }
}
