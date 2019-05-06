/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/4
 * Credits to Timo Janz aka Summerfeeling
 */

package systems.kinau.fishingbot.io.discord;

import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;

public class DiscordMessageDispatcher {

    private TemmieWebhook webHook;

    public DiscordMessageDispatcher(String webHook) {
        this.webHook = new TemmieWebhook(webHook);
    }

    public void dispatchMessage(String content, DiscordDetails details) {
        DiscordMessage.DiscordMessageBuilder builder = DiscordMessage.builder()
                .username(details.getUserName())
                .content(content);
        if (details.getAvatar() != null && !details.getAvatar().isEmpty()) builder.avatarUrl(details.getAvatar());

        webHook.sendMessage(builder.build());
    }
}
