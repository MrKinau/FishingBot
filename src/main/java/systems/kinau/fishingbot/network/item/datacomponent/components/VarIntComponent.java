package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class VarIntComponent extends DataComponent {

    private int value;

    public VarIntComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(value, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.value = Packet.readVarInt(in);
    }
}
