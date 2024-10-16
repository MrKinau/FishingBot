package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.play.UpdateWindowItemsEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PacketInWindowItems extends Packet {

    private int windowId;
    private List<Slot> slots;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.windowId = readContainerIdUnsigned(in, protocolId);
        if (protocolId >= ProtocolConstants.MC_1_17_1) {
            readVarInt(in); // revision (whatever it is?) or arbitrary state id?
        }
        this.slots = new ArrayList<>();
        int count = protocolId >= ProtocolConstants.MC_1_17_1 ? readVarInt(in) : in.readShort();
        for (int i = 0; i < count; i++) {
            this.slots.add(readSlot(in, protocolId, networkHandler.getDataComponentRegistry()));
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateWindowItemsEvent(windowId, slots));
    }
}
