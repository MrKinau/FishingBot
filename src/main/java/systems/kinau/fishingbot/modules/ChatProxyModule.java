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

import java.util.Scanner;

public class ChatProxyModule extends Module implements Listener {

    private Thread chatThread;

    @Override
    public void onEnable() {
        MineBot.getInstance().getEventManager().registerListener(this);
        chatThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(!chatThread.isInterrupted()){
                String line = scanner.nextLine();
                MineBot.getInstance().getNet().sendPacket(new PacketOutChat(line));
            }
        });
        chatThread.start();
    }

    @Override
    public void onDisable() {
        chatThread.interrupt();
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && !"".equals(event.getText()))
            MineBot.getLog().info("[CHAT] " + event.getText());
    }
}
