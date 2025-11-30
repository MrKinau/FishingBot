package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class UseEffectsComponent extends DataComponent {

    private boolean canSprint;
    private boolean vibrations;
    private float speed;

    public UseEffectsComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(canSprint);
        out.writeBoolean(vibrations);
        out.writeFloat(speed);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.canSprint = in.readBoolean();
        this.vibrations = in.readBoolean();
        this.speed = in.readFloat();
    }
}
