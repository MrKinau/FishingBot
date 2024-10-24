package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class HolderSetComponent extends DataComponent {

    private HolderSetComponentPart holderSet;

    public HolderSetComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        holderSet.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.holderSet = new HolderSetComponentPart();
        holderSet.read(in, protocolId);
    }
}
