package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class BooleanComponent extends DataComponent {

    private boolean value;

    public BooleanComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(value);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.value = in.readBoolean();
    }
}
