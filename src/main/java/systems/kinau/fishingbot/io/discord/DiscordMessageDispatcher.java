/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/4
 * Credits to Timo Janz aka Summerfeeling
 */

package systems.kinau.fishingbot.io.discord;


import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class DiscordMessageDispatcher {

    private WebhookClient webHook;

    public DiscordMessageDispatcher(String webHook) {
        this.webHook = WebhookClient.withUrl(webHook);
    }

    public void dispatchMessage(String content, DiscordDetails details) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(details.getUserName())
                .setContent(content);
        if (details.getAvatar() != null && !details.getAvatar().isEmpty())
            builder.setAvatarUrl(details.getAvatar());

        webHook.send(builder.build());
    }
}
