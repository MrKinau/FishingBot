package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.HashMap;
import java.util.Map;

public class TrimComponent extends DataComponent {

    private TrimMaterial trimMaterial;
    private TrimPattern trimPattern;
    private boolean showInTooltip;

    public TrimComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        trimMaterial.write(out, protocolId);
        trimPattern.write(out, protocolId);
        out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        TrimMaterial trimMaterial = new TrimMaterial();
        trimMaterial.read(in, protocolId);
        this.trimMaterial = trimMaterial;

        TrimPattern trimPattern = new TrimPattern();
        trimPattern.read(in, protocolId);
        this.trimPattern = trimPattern;

        this.showInTooltip = in.readBoolean();
    }

    @Getter
    @NoArgsConstructor
    public static class TrimMaterial implements DataComponentPart {

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

    @Getter
    @NoArgsConstructor
    public static class TrimPattern implements DataComponentPart {

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
                this.templateItem = Packet.readVarInt(in);
                this.description = Packet.readNBT(in, protocolId);
                this.decal = in.readBoolean();
            }
        }
    }
}
