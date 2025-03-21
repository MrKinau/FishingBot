package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.trim.TrimMaterial;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class ProvidesTrimMaterialComponent extends DataComponent {

    private boolean streamCodec;

    private String trimMaterialId;
    private TrimMaterial trimMaterial;

    public ProvidesTrimMaterialComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(streamCodec);
        if (streamCodec) {
            trimMaterial.write(out, protocolId);
        } else {
            Packet.writeString(trimMaterialId, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.streamCodec = in.readBoolean();
        if (streamCodec) {
            this.trimMaterial = new TrimMaterial();
            trimMaterial.read(in, protocolId);
        } else {
            this.trimMaterialId = Packet.readString(in);
        }
    }
}
