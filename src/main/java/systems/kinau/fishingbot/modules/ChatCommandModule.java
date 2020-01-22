/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;

public class ChatCommandModule extends Module implements Listener {

    @Override
    public void onEnable() {
        FishingBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && event.getText().contains(FishingBot.getInstance().getAuthData().getUsername() + ", Level?")) {
            FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(FishingBot.getInstance().getPlayer().getLevels() + " Level, Sir!"));
        }
    }
}
