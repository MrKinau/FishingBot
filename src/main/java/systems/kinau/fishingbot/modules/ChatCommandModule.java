/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;

public class ChatCommandModule extends Module implements Listener {

    @Override
    public void onEnable() {
        MineBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        MineBot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && event.getText().contains(MineBot.getInstance().getAuthData().getUsername() + ", Level?")) {
            MineBot.getInstance().getNet().sendPacket(new PacketOutChat(MineBot.getInstance().getPlayer().getLevels() + " Level, Sir!"));
        }
    }
}
