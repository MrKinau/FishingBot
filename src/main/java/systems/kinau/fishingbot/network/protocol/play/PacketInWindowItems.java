package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.bot.ItemStack;
import systems.kinau.fishingbot.event.play.WindowItemsEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PacketInWindowItems extends Packet {

    @Getter private int windowId;
    @Getter private Map<Integer, ItemStack> slotData = new HashMap<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.windowId = in.readUnsignedByte();
        int count = in.readShort();
        for (int i = 0; i < count; i++) {
            ItemStack itemStack = readItemstack(in);
            if(itemStack != null)
                slotData.put(i, itemStack);
        }

        MineBot.getInstance().getEventManager().callEvent(new WindowItemsEvent(getWindowId(), getSlotData()));
    }
}
