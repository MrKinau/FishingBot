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
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.ChatComponentUtils;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PacketInChatPlayer extends Packet {

    private String text;
    private UUID sender;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId < ProtocolConstants.MINECRAFT_1_19) {
            this.text = readChatComponent(in, protocolId);
            if (text != null)
                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
        } else {
            try {
                if (protocolId >= ProtocolConstants.MINECRAFT_1_19_3) {
                    this.sender = readUUID(in); // sender
                    readVarInt(in); // index
                }
                if (in.readBoolean()) {
                    if (protocolId >= ProtocolConstants.MINECRAFT_1_19_3) {
                        in.skipBytes(256);
                    } else {
                        int sigLength = readVarInt(in);
                        in.skipBytes(sigLength);
                    }
                }
                if (protocolId <= ProtocolConstants.MINECRAFT_1_19_1) {
                    this.sender = readUUID(in);
                    int sigLength = readVarInt(in);
                    in.skipBytes(sigLength);
                }
                String actualMessage = readString(in); //plain
                if (protocolId <= ProtocolConstants.MINECRAFT_1_19_1 && in.readBoolean())
                    readString(in);
                in.readLong();
                in.readLong();
                int prevMsgs = readVarInt(in);
                for (int i = 0; i < prevMsgs; i++) {
                    if (protocolId <= ProtocolConstants.MINECRAFT_1_19_1) {
                        this.sender = readUUID(in);
                        int prevMsgSig = readVarInt(in);
                        in.skipBytes(prevMsgSig);
                    } else {
                        int index = readVarInt(in);
                        if (index == 0)
                            in.skipBytes(256);
                    }
                }
                // unsigned content
                if (in.readBoolean())
                    readChatComponent(in, protocolId);
                int filterMask = readVarInt(in);
                if (filterMask == 2) {
                    int bitSetLength = readVarInt(in);
                    for (int i = 0; i < bitSetLength; i++)
                        in.readLong();
                }
                int chatType = readVarInt(in); // chat type
                String userName = readChatComponent(in, protocolId);
                String targetName = "";
                // target name
                if (in.readBoolean())
                    targetName = readChatComponent(in, protocolId);
                this.text = ChatComponentUtils.sillyTransformWithChatType(protocolId, chatType, userName, targetName, actualMessage);

                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ChatEvent(getText(), getSender()));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
