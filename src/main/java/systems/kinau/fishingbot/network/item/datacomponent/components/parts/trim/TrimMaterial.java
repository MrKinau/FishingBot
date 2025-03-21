package systems.kinau.fishingbot.network.item.datacomponent.components.parts.trim;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class TrimMaterial implements DataComponentPart {

    private int materialId;
    private NBTTag description;

    private String assetName;
    private int ingredient;
    private float itemModelIndex;
    private Map<Integer, String> overrideArmorMaterials = new HashMap<>();
    private Map<String, String> overrideArmorMaterialsNew = new HashMap<>();

    private String baseSuffix;
    private Map<String, String> overrides = new HashMap<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(materialId, out);
        if (materialId == 0) {
            if (protocolId >= ProtocolConstants.MC_1_21_5_RC_1) {
                Packet.writeString(baseSuffix, out);
                Packet.writeVarInt(overrides.size(), out);
                overrides.forEach((key, value) -> {
                    Packet.writeString(key, out);
                    Packet.writeString(value, out);
                });
            } else {
                Packet.writeString(assetName, out);
                Packet.writeVarInt(ingredient, out);
                out.writeFloat(itemModelIndex);
                if (protocolId >= ProtocolConstants.MC_1_21_2) {
                    Packet.writeVarInt(overrideArmorMaterialsNew.size(), out);
                    overrideArmorMaterialsNew.forEach((key, value) -> {
                        Packet.writeString(key, out);
                        Packet.writeString(value, out);
                    });
                } else {
                    Packet.writeVarInt(overrideArmorMaterials.size(), out);
                    overrideArmorMaterials.forEach((key, value) -> {
                        Packet.writeVarInt(key, out);
                        Packet.writeString(value, out);
                    });
                }
            }
            Packet.writeNBT(description, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.materialId = Packet.readVarInt(in);
        if (materialId == 0) {
            if (protocolId >= ProtocolConstants.MC_1_21_5_RC_1) {
                this.baseSuffix = Packet.readString(in);
                int count = Packet.readVarInt(in);
                this.overrides = new HashMap<>(count);
                for (int i = 0; i < count; i++) {
                    String asset = Packet.readString(in);
                    String assetSuffix = Packet.readString(in);
                    overrides.put(asset, assetSuffix);
                }
            } else {
                this.assetName = Packet.readString(in);
                this.ingredient = Packet.readVarInt(in);
                this.itemModelIndex = in.readFloat();
                if (protocolId >= ProtocolConstants.MC_1_21_2)
                    this.overrideArmorMaterialsNew = new HashMap<>();
                else
                    this.overrideArmorMaterials = new HashMap<>();
                int count = Packet.readVarInt(in);
                for (int i = 0; i < count; i++) {
                    if (protocolId >= ProtocolConstants.MC_1_21_2) {
                        String key = Packet.readString(in);
                        String value = Packet.readString(in);
                        overrideArmorMaterialsNew.put(key, value);
                    } else {
                        int key = Packet.readVarInt(in);
                        String value = Packet.readString(in);
                        overrideArmorMaterials.put(key, value);
                    }
                }
            }
            this.description = Packet.readNBT(in, protocolId);
        }
    }
}