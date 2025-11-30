package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class AttackRangeComponent extends DataComponent {

    private float minRange;
    private float maxRange;
    private float hitboxMargin;
    private float mobFactor;

    public AttackRangeComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(minRange);
        out.writeFloat(maxRange);
        out.writeFloat(hitboxMargin);
        out.writeFloat(mobFactor);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.minRange = in.readFloat();
        this.maxRange = in.readFloat();
        this.hitboxMargin = in.readFloat();
        this.mobFactor = in.readFloat();
    }
}
