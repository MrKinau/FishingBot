package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.EitherVarIntOrIdentifier;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class DamageTypeComponent extends DataComponent {

    private EitherVarIntOrIdentifier damageType;

    public DamageTypeComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        damageType.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.damageType = new EitherVarIntOrIdentifier();
        damageType.read(in, protocolId);
    }
}
