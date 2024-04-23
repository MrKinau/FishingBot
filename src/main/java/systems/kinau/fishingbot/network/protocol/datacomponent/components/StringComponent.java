package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class StringComponent extends DataComponent {

    private String value;

    public StringComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeString(value, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.value = Packet.readString(in);
    }
}
