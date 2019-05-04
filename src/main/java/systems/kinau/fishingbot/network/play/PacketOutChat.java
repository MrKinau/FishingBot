/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.io.discord.DiscordDetails;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
public class PacketOutChat extends Packet {

    @Getter private String message;

    @Override
    public void write(ByteArrayDataOutput out) {
        writeString(getMessage(), out);
        if(!FishingBot.getConfig().getWebHook().equalsIgnoreCase("false"))
            FishingBot.getDiscord().dispatchMessage("`" + getMessage() + "`",
                    new DiscordDetails("FishingBot", "https://vignette.wikia.nocookie.net/mcmmo/images/2/2f/FishingRod.png/revision/latest?cb=20110822134546"));

    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) { }
}
