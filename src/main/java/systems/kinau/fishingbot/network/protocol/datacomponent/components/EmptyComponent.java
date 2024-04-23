package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class EmptyComponent extends DataComponent {

    public EmptyComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {}

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {}
}
