package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.EitherVarIntOrIdentifier;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class EitherVarIntOrIdentifierComponent extends DataComponent {

    private EitherVarIntOrIdentifier either;

    public EitherVarIntOrIdentifierComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        either.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.either = new EitherVarIntOrIdentifier();
        either.read(in, protocolId);
    }
}
