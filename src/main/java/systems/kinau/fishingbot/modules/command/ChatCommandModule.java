/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules.command;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.modules.Module;

public class ChatCommandModule extends Module implements Listener {

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!isEnabled())
            return;
        String userName = FishingBot.getInstance().getCurrentBot().getAuthData().getUsername();
        if (event.getText().contains(userName) && event.getText().contains(",")) {
            String[] parts = event.getText().split(",");
            if (parts.length <= 1)
                return;

            StringBuilder cmdBuilder = new StringBuilder(parts[1]);
            for (int i = 2; i < parts.length; i++)
                cmdBuilder.append(parts[i]);

            String fullCommand = cmdBuilder.toString().trim();
            if (Character.isWhitespace(fullCommand.charAt(0)))
                fullCommand = fullCommand.substring(1);

            FishingBot.getInstance().getCurrentBot().getCommandRegistry().dispatchCommand(fullCommand, CommandExecutor.OTHER_PLAYER);
        }
    }
}
