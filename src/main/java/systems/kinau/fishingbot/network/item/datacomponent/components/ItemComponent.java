package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentRegistry;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ItemComponent extends DataComponent {

    private final DataComponentRegistry dataComponentRegistry;

    private Slot item = Slot.EMPTY;

    public ItemComponent(DataComponentRegistry dataComponentRegistry, int componentTypeId) {
        super(componentTypeId);
        this.dataComponentRegistry = dataComponentRegistry;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeSlot(item, out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (FishingBot.getInstance().getConfig().isLogItemData()) {
            FishingBot.getLog().info("Start reading single item component (" + getComponentTypeId() + ")");
        }
        this.item = Packet.readSlot(in, protocolId, dataComponentRegistry);
        if (FishingBot.getInstance().getConfig().isLogItemData()) {
            FishingBot.getLog().info("End of reading single item component (" + getComponentTypeId() + ")");
        }
    }
}
