package systems.kinau.fishingbot.network.item.datacomponent.components.parts.attributemodifier;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeModifierDisplay implements DataComponentPart {

    private int displayId;
    private NBTTag overrideText;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(displayId, out);
        if (displayId == 2) {
            Packet.writeNBT(overrideText, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.displayId = Packet.readVarInt(in);
        // override
        if (displayId == 2) {
            this.overrideText = Packet.readNBT(in, protocolId);
        }
    }
}
