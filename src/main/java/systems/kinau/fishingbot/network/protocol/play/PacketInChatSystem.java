/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5th 2019
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.TextComponent;

@NoArgsConstructor
public class PacketInChatSystem extends Packet {

    private final JSONParser PARSER = new JSONParser();
    @Getter private String text;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        this.text = readString(in);
        try {
            JSONObject object = (JSONObject) PARSER.parse(text);

            try {
                this.text = TextComponent.toPlainText(object);
            } catch (Exception ignored) {
                //Ignored
            }

            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), null));
        } catch (Exception ignored) {
            //Ignored
        }
    }
}
