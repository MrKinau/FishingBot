package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.TypedEntityData;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class TypedEntityDataComponent extends DataComponent {

    private TypedEntityData typedEntityData;

    public TypedEntityDataComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        typedEntityData.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.typedEntityData = new TypedEntityData();
        typedEntityData.read(in, protocolId);
    }
}
