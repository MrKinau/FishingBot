/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.play.UpdateSlotEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
@Getter
public class PacketInSetSlot extends Packet {

    private int windowId;
    private short slotId;
    private Slot slot;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        this.windowId = in.readByte();
        if (protocolId >= ProtocolConstants.MC_1_17_1) {
            readVarInt(in); // revision
        }
        this.slotId = in.readShort();
        this.slot = readSlot(in, protocolId, networkHandler.getDataComponentRegistry());

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateSlotEvent(windowId, slotId, slot));
    }
}
