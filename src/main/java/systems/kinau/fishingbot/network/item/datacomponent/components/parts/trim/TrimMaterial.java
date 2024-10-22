package systems.kinau.fishingbot.network.item.datacomponent.components.parts.trim;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class TrimMaterial implements DataComponentPart {

    private int materialId;
    private String assetName;
    private int ingredient;
    private float itemModelIndex;
    private Map<Integer, String> overrideArmorMaterials = new HashMap<>();
    private NBTTag description;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(materialId, out);
        if (materialId == 0) {
            Packet.writeString(assetName, out);
            Packet.writeVarInt(ingredient, out);
            out.writeFloat(itemModelIndex);
            Packet.writeVarInt(overrideArmorMaterials.size(), out);
            overrideArmorMaterials.forEach((key, value) -> {
                Packet.writeVarInt(key, out);
                Packet.writeString(value, out);
            });
            Packet.writeNBT(description, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.materialId = Packet.readVarInt(in);
        if (materialId == 0) {
            this.assetName = Packet.readString(in);
            this.ingredient = Packet.readVarInt(in);
            this.itemModelIndex = in.readFloat();
            this.overrideArmorMaterials = new HashMap<>();
            int count = Packet.readVarInt(in);
            for (int i = 0; i < count; i++) {
                int key = Packet.readVarInt(in);
                String value = Packet.readString(in);
                overrideArmorMaterials.put(key, value);
            }
            this.description = Packet.readNBT(in, protocolId);
        }
    }
}