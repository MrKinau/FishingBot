/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/4
 * Credits to Timo Janz aka Summerfeeling
 */

package systems.kinau.fishingbot.io.discord;

import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import org.apache.commons.lang3.Validate;

public class DiscordMessageDispatcher {

    private TemmieWebhook webHook;

    public DiscordMessageDispatcher(String webHook) {
        this.webHook = new TemmieWebhook(webHook);
    }

    public void dispatchMessage(String content, DiscordDetails details) {
        Validate.notNull(webHook, "Webhook cannot be null");
        Validate.notBlank(details.getUserName(), "Username cannot be null or empty");
        Validate.notBlank(content, "Content cannot be null or empty");

        DiscordMessage.DiscordMessageBuilder builder = DiscordMessage.builder()
                .username(details.getUserName())
                .content(content);
        if (details.getAvatar() != null && !details.getAvatar().isEmpty()) builder.avatarUrl(details.getAvatar());

        webHook.sendMessage(builder.build());
    }
}
