package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.ResolvableFloat;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.ResolvableInt;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class CookingFuelComponent extends DataComponent {

    private ResolvableInt burnTime;
    private ResolvableFloat speedMultiplier;

    public CookingFuelComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        burnTime.write(out, protocolId);
        speedMultiplier.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.burnTime = new ResolvableInt();
        burnTime.read(in, protocolId);
        this.speedMultiplier = new ResolvableFloat();
        speedMultiplier.read(in, protocolId);
    }
}
