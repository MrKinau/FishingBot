package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class EmptyComponent extends DataComponent {

    public EmptyComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // no content in empty component
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        // no content in empty component
    }
}
