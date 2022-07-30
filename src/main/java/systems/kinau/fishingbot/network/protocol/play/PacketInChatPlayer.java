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
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.TextComponent;

import java.util.UUID;

@NoArgsConstructor
public class PacketInChatPlayer extends Packet {

    private final JSONParser PARSER = new JSONParser();
    @Getter private String text;
    @Getter private UUID sender;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId >= ProtocolConstants.MINECRAFT_1_19) {

        } else {
            this.text = readString(in);
            try {
                JSONObject object = (JSONObject) PARSER.parse(text);

                try {
                    this.text = TextComponent.toPlainText(object);
                } catch (Exception ignored) {
                    //Ignored
                }

                //TODO: Handle this correctly. This packet represents the normal chat packet up to 1.18.2 and the vanilla server player chat packet in 1.19 and higher

                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
            } catch (Exception ignored) {
                //Ignored
            }
        }
    }
}
