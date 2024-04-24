package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class SimpleMapperComponent extends DataComponent {

    private final DataComponentPart part;

    public SimpleMapperComponent(DataComponentPart part, int componentTypeId) {
        super(componentTypeId);
        this.part = part;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        part.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        part.read(in, protocolId);
    }
}
