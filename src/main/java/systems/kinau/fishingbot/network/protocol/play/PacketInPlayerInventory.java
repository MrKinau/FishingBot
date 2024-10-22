package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.play.UpdateSlotEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class PacketInPlayerInventory extends Packet {

    private int slotId;
    private Slot item;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.slotId = readVarInt(in);
        if (FishingBot.getInstance().getConfig().isLogItemData()) {
            FishingBot.getLog().info("Start reading PacketInPlayerInventory slot");
        }
        this.item = readSlot(in, protocolId, networkHandler.getDataComponentRegistry());
        if (FishingBot.getInstance().getConfig().isLogItemData()) {
            FishingBot.getLog().info("End of reading PacketInPlayerInventory slot");
            if (in.getAvailable() > 0)
                FishingBot.getLog().warning("End of reading PacketInPlayerInventory has " + in.getAvailable() + " byte(s) left");
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateSlotEvent(0, Integer.valueOf(slotId).shortValue(), item));
    }
}
