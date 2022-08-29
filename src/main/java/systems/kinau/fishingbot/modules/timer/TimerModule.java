package systems.kinau.fishingbot.modules.timer;

import systems.kinau.fishingbot.Bot;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.Module;

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
                    Bot bot = FishingBot.getInstance().getCurrentBot();
                    if (bot == null)
                        continue;
                    bot.runCommand(command, true);
                }
            }, timer.getUnits(), timer.getUnits(), timer.getTimeUnit()));
        });
    }
}
