package systems.kinau.fishingbot.network.item.datacomponent.components.parts.attributemodifier;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeModifier implements DataComponentPart {

    private int attributeId;
    private UUID uuid;
    private String name;
    private double amount;
    private int operationId;
    private int equipmentSlotGroupId;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(getAttributeId(), out);

        if (protocolId < ProtocolConstants.MC_1_21)
            Packet.writeUUID(getUuid(), out);
        Packet.writeString(getName(), out);
        out.writeDouble(getAmount());
        Packet.writeVarInt(getOperationId(), out);

        Packet.writeVarInt(getEquipmentSlotGroupId(), out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.attributeId = Packet.readVarInt(in);

        if (protocolId < ProtocolConstants.MC_1_21)
            this.uuid = Packet.readUUID(in);
        this.name = Packet.readString(in);
        this.amount = in.readDouble();
        this.operationId = Packet.readVarInt(in);

        this.equipmentSlotGroupId = Packet.readVarInt(in);
    }
}