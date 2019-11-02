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
        FishingBot.getLog().info("Module \"" + this.getClass().getSimpleName() + "\" enabled!");
    }

    public void disable() {
        this.enabled = false;
        onDisable();
        FishingBot.getLog().info("Module \"" + this.getClass().getSimpleName() + "\" disabled!");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public abstract void onEnable();

    public abstract void onDisable();

}
