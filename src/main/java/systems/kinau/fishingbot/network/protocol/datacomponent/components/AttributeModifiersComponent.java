package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class AttributeModifiersComponent extends DataComponent {

    private List<AttributeModifier> modifiers = Collections.emptyList();
    private boolean showInTooltip = true;

    public AttributeModifiersComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(modifiers.size(), out);
        for (AttributeModifier modifier : modifiers) {
            modifier.write(out, protocolId);
        }

        out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        // modifiers
        this.modifiers = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            AttributeModifier modifier = new AttributeModifier();
            modifier.read(in, protocolId);
            modifiers.add(modifier);
        }

        // show_in_tooltip
        this.showInTooltip = in.readBoolean();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttributeModifier implements DataComponentPart {
        private int attributeId;
        private UUID uuid;
        private String name;
        private double amount;
        private int operationId;
        private int equipmentSlotGroupId;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(getAttributeId(), out);

            Packet.writeUUID(getUuid(), out);
            Packet.writeString(getName(), out);
            out.writeDouble(getAmount());
            Packet.writeVarInt(getOperationId(), out);

            Packet.writeVarInt(getEquipmentSlotGroupId(), out);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.attributeId = Packet.readVarInt(in);

            this.uuid = Packet.readUUID(in);
            this.name = Packet.readString(in);
            this.amount = in.readDouble();
            this.operationId = Packet.readVarInt(in);

            this.equipmentSlotGroupId = Packet.readVarInt(in);
        }
    }
}
