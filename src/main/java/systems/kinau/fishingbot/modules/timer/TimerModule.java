package systems.kinau.fishingbot.modules.timer;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.Module;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class TimerModule extends Module {

    private List<Timer> enabledTimers;
    private List<ScheduledFuture<?>> runningTimers;

    @Override
    public void onEnable() {
        this.enabledTimers = FishingBot.getInstance().getCurrentBot().getConfig().getTimers();
        this.runningTimers = new ArrayList<>();
        startTimers();
    }

    @Override
    public void onDisable() {
        runningTimers.forEach(scheduledFuture -> scheduledFuture.cancel(true));
    }

    private void startTimers() {
        enabledTimers.forEach(timer -> {
            runningTimers.add(FishingBot.getScheduler().scheduleAtFixedRate(() -> {
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
            }, timer.getUnits(), timer.getUnits(), timer.getTimeUnit()));
        });
    }
}
