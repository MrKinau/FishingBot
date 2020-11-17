/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.command.CommandExecutor;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;

import java.util.Scanner;

public class ChatProxyModule extends Module implements Listener {

    private Thread chatThread;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        chatThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(!chatThread.isInterrupted()){
                String line = scanner.nextLine();
                if (line.startsWith("/")) {
                    boolean executed = FishingBot.getInstance().getCurrentBot().getCommandRegistry().dispatchCommand(line, CommandExecutor.CONSOLE);
                    if (executed)
                        continue;
                }
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChat(line));
            }
        });
        chatThread.start();
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && !"".equals(event.getText()))
           FishingBot.getI18n().info("module-chat-proxy-chat-message", event.getText());
    }
}
