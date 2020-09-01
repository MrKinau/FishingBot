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
        FishingBot.getInstance().getEventManager().registerListener(this);
        chatThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(!chatThread.isInterrupted()){
                String line = scanner.nextLine();
                if (line.startsWith("/")) {
                    boolean executed = FishingBot.getInstance().getCommandRegistry().dispatchCommand(line, CommandExecutor.CONSOLE);
                    if (!executed)
                        FishingBot.getLog().info("This command does not exist. Try /help for a list of commands.");
                } else
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(line));
            }
        });
        chatThread.start();
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && !"".equals(event.getText()))
            FishingBot.getLog().info("[CHAT] " + event.getText());
    }
}
