/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatProxyModule extends Module implements Listener {

    private Thread chatThread;
    private BufferedReader scanner;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        chatThread = new Thread(() -> {
            scanner = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while (!chatThread.isInterrupted()) {
                    while (!scanner.ready() && !chatThread.isInterrupted()) {
                        Thread.sleep(100);
                    }
                    line = scanner.readLine();
                    if (FishingBot.getInstance().getCurrentBot() == null) return;
                    FishingBot.getInstance().getCurrentBot().runCommand(line, true);
                }
            } catch (IOException | InterruptedException ignored) { }
        });
        chatThread.setName("chatThread");
        chatThread.start();
    }

    @Override
    public void onDisable() {
        chatThread.interrupt();
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && !"".equals(event.getText()))
           FishingBot.getI18n().info("module-chat-proxy-chat-message", event.getText());
    }
}
