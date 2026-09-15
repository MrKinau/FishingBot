package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.ResolvableInt;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class CompostableComponent extends DataComponent {

    private ResolvableInt layers;

    public CompostableComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        layers.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.layers = new ResolvableInt();
        layers.read(in, protocolId);
    }
}
