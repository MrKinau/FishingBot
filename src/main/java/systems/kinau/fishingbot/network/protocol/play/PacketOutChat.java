/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.io.discord.DiscordDetails;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
public class PacketOutChat extends Packet {

    @Getter private String message;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(getMessage(), out);
        if (FishingBot.getInstance().getConfig().isWebHookEnabled()
                && !FishingBot.getInstance().getConfig().getWebHook().equalsIgnoreCase("false")
                && !FishingBot.getInstance().getConfig().getWebHook().equals("YOURWEBHOOK")) {
            FishingBot.getInstance().getDiscord().dispatchMessage("`" + getMessage() + "`",
                    new DiscordDetails("FishingBot", "https://vignette.wikia.nocookie.net/mcmmo/images/2/2f/FishingRod.png"));
        }

    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}
