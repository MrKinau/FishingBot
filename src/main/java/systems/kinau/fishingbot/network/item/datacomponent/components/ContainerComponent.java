package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentRegistry;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ContainerComponent extends DataComponent {

    private final DataComponentRegistry dataComponentRegistry;
    private List<Optional<Slot>> items = Collections.emptyList();

    public ContainerComponent(DataComponentRegistry dataComponentRegistry, int componentTypeId) {
        super(componentTypeId);
        this.dataComponentRegistry = dataComponentRegistry;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(items.size(), out);
        for (Optional<Slot> item : items) {
            if (protocolId >= ProtocolConstants.MC_26_1)
                out.writeBoolean(item.isPresent());
            if (!item.isPresent()) continue;
            Packet.writeSlot(item.get(), out, protocolId, protocolId >= ProtocolConstants.MC_26_1);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.items = new LinkedList<>();
        int count = Packet.readVarInt(in);
        if (count <= 0) return;
        if (FishingBot.getInstance().getConfig().isLogItemData()) {
            FishingBot.getLog().info("Start reading container component with " + count + " elements");
        }
        for (int i = 0; i < count; i++) {
            if (protocolId >= ProtocolConstants.MC_26_1 && !in.readBoolean()) {
                items.add(Optional.empty());
                continue;
            }
            items.add(Optional.of(Packet.readSlot(in, protocolId, dataComponentRegistry, protocolId >= ProtocolConstants.MC_26_1)));
        }
        if (FishingBot.getInstance().getConfig().isLogItemData()) {
            FishingBot.getLog().info("End of reading container component with " + count + " elements");
        }
    }
}
