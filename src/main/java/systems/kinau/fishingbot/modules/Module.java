/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;

public abstract class Module {

    private boolean enabled = false;

    public void enable() {
        this.enabled = true;
        onEnable();

        FishingBot.getI18n().info("module-enabled", getClass().getSimpleName());
    }

    public void disable() {
        this.enabled = false;
        onDisable();

        FishingBot.getI18n().info("module-disabled", getClass().getSimpleName());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public abstract void onEnable();

    public abstract void onDisable();

}
