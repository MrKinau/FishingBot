package systems.kinau.fishingbot.network.protocol.common;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.common.ResourcePackEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PacketInResourcePack extends Packet {

    private UUID uuid;
    private String url;
    private String hash;
    private boolean forced;
    private String prompt;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        if (protocolId >= ProtocolConstants.MINECRAFT_1_20_3)
            this.uuid = readUUID(in);
        this.url = readString(in);
        this.hash = readString(in);
        this.forced = in.readBoolean();
        if (in.readBoolean())
            this.prompt = readChatComponent(in, protocolId);
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new ResourcePackEvent(uuid, url, hash, forced, prompt));
    }
}
