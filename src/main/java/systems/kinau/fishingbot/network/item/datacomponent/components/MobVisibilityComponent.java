package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class MobVisibilityComponent extends DataComponent {

    private HolderSetComponentPart targetingEntityTypes;
    private float visibility;

    public MobVisibilityComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        targetingEntityTypes.write(out, protocolId);
        out.writeFloat(visibility);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.targetingEntityTypes = new HolderSetComponentPart();
        targetingEntityTypes.read(in, protocolId);
        this.visibility = in.readFloat();
    }
}
