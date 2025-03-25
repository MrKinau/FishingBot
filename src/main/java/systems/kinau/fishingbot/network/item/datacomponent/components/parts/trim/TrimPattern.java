package systems.kinau.fishingbot.network.item.datacomponent.components.parts.trim;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@Getter
@NoArgsConstructor
public class TrimPattern implements DataComponentPart {

    private int patternId;
    private String assetId;
    private int templateItem;
    private NBTTag description;
    private boolean decal;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(patternId, out);
        if (patternId == 0) {
            Packet.writeString(assetId, out);
            if (protocolId < ProtocolConstants.MC_1_21_5)
                Packet.writeVarInt(templateItem, out);
            Packet.writeNBT(description, out);
            out.writeBoolean(decal);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.patternId = Packet.readVarInt(in);
        if (patternId == 0) {
            this.assetId = Packet.readString(in);
            if (protocolId < ProtocolConstants.MC_1_21_5)
                this.templateItem = Packet.readVarInt(in);
            this.description = Packet.readNBT(in, protocolId);
            this.decal = in.readBoolean();
        }
    }
}