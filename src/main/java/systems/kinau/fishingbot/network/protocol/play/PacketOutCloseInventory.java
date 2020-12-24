package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.InventoryCloseEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutCloseInventory extends Packet {

    @Getter private int windowId;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeByte(windowId);

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new InventoryCloseEvent(getWindowId()));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }

}
