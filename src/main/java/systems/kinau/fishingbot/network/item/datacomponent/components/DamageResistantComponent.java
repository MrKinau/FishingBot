package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class DamageResistantComponent extends DataComponent {

    private String tagKey;
    private HolderSetComponentPart holderSet;

    public DamageResistantComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId < ProtocolConstants.MC_26_1) {
            Packet.writeString(tagKey, out);
        } else {
            holderSet.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (protocolId < ProtocolConstants.MC_26_1) {
            this.tagKey = Packet.readString(in);
        } else {
            this.holderSet = new HolderSetComponentPart();
            holderSet.read(in, protocolId);
        }
    }
}
