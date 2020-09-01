/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutClickWindow;

//TODO: Implement Command-System (Commands in console and MC-Chat)
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
        String userName = FishingBot.getInstance().getAuthData().getUsername();
        if (isEnabled() && event.getText().contains(userName + ", Level?")) {
            FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(FishingBot.getInstance().getPlayer().getLevels() + " Level, Sir!"));
        } else if (isEnabled() && (event.getText().contains(userName + ", empty inventory"))
                || event.getText().contains(userName + ", clear inventory")
                || event.getText().contains(userName + ", drop inventory")) {
            FishingBot.getLog().info("dropping inventory");
            for (short slotId = 9; slotId <= 44; slotId++) {
                if (slotId == FishingBot.getInstance().getPlayer().getHeldSlot())
                    continue;
                FishingBot.getInstance().getNet().sendPacket(
                        new PacketOutClickWindow(
                                /* player inventory */ 0,
                                slotId,
                                /* drop entire stack */ (byte) 1,
                                /* action count starting at 1 */ (short) (slotId - 8),
                                /* drop entire stack */ 4,
                                /* empty slot */ new Slot(false, -1, (byte) -1, (short) -1, new byte[]{0})
                        )
                );
            }
        }
    }
}
