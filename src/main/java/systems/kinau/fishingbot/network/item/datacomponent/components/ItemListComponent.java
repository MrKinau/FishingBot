package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentRegistry;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ItemListComponent extends DataComponent {

    private final DataComponentRegistry dataComponentRegistry;
    private List<Slot> items = Collections.emptyList();

    public ItemListComponent(DataComponentRegistry dataComponentRegistry, int componentTypeId) {
        super(componentTypeId);
        this.dataComponentRegistry = dataComponentRegistry;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(items.size(), out);
        for (Slot item : items) {
            Packet.writeSlot(item, out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.items = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            this.items.add(Packet.readSlot(in, protocolId, dataComponentRegistry));
        }
    }
}
