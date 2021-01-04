package systems.kinau.fishingbot.modules.timer;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.Module;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

import java.util.List;

public class TimerModule extends Module {

    private List<Timer> enabledTimers;

    @Override
    public void onEnable() {
        this.enabledTimers = FishingBot.getInstance().getCurrentBot().getConfig().getTimers();
        startTimers();
    }

    @Override
    public void onDisable() { }

    private void startTimers() {
        enabledTimers.forEach(timer -> {
            FishingBot.getScheduler().scheduleAtFixedRate(() -> {
                for (String command : timer.getCommands()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (FishingBot.getInstance().getCurrentBot().getCommandRegistry().dispatchCommand(command, CommandExecutor.UNSET))
                        continue;
                    FishingBot.getInstance().getCurrentBot().getPlayer().sendMessage(command);
                }

            }, timer.getUnits(), timer.getUnits(), timer.getTimeUnit());
        });
    }
}
