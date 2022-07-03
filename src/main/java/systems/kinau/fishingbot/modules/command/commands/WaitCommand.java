package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class WaitCommand extends Command {

    public WaitCommand() {
        super("wait", FishingBot.getI18n().t("command-wait-desc"), "sleep");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        try {
            if (args.length != 1)
                throw new IllegalArgumentException();

            int time = Integer.parseInt(args[0]);

            try {
                Thread.sleep(time * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sendMessage(executor, "command-wait-waited", time);
        } catch (IllegalArgumentException ex) {
            sendMessage("/wait <time in seconds>", executor);
        }
    }
}
