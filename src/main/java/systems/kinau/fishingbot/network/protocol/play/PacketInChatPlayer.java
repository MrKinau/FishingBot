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
    @Getter
    private String text;
    @Getter
    private UUID sender;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId >= ProtocolConstants.MINECRAFT_1_19) {
            try {
                if (in.readBoolean()) {
                    int sigLength = readVarInt(in);
                    in.skipBytes(sigLength);
                }
                readUUID(in);
                int sigLength = readVarInt(in);
                in.skipBytes(sigLength);
                String actualMessage = readString(in); //plain
                if (in.readBoolean())
                    readString(in);
                in.readLong();
                in.readLong();
                int prevMsgs = readVarInt(in);
                for (int i = 0; i < prevMsgs; i++) {
                    readUUID(in);
                    int prevMsgSig = readVarInt(in);
                    in.skipBytes(prevMsgSig);
                }
                if (in.readBoolean())
                    readString(in);
                int filterMask = readVarInt(in);
                if (filterMask == 2) {
                    int bitSetLength = readVarInt(in);
                    for (int i = 0; i < bitSetLength; i++)
                        in.readLong();
                }
                readVarInt(in);
                String userName = readChatComponent(in);
                this.text = "<" + userName + "> " + actualMessage;
                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
                return;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        this.text = readChatComponent(in);
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
    }

    private String readChatComponent(ByteArrayDataInputWrapper in) {
        String text = readString(in);
        try {
            JSONObject object = (JSONObject) PARSER.parse(text);

            try {
                text = TextComponent.toPlainText(object);
            } catch (Exception ignored) {
                //Ignored
            }

            //TODO: Handle this correctly. This packet represents the normal chat packet up to 1.18.2 and the vanilla server player chat packet in 1.19 and higher
        } catch (Exception ignored) {
            //Ignored
        }
        return text;
    }
}
