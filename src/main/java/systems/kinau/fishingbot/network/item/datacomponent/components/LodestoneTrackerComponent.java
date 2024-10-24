package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.lodestone.GlobalPos;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class LodestoneTrackerComponent extends DataComponent {

    private Optional<GlobalPos> target;
    private boolean tracked;

    public LodestoneTrackerComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(target.isPresent());
        target.ifPresent(globalPos -> globalPos.write(out, protocolId));
        out.writeBoolean(tracked);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (in.readBoolean()) {
            GlobalPos globalPos = new GlobalPos();
            globalPos.read(in, protocolId);
            this.target = Optional.of(globalPos);
        } else {
            this.target = Optional.empty();
        }
        this.tracked = in.readBoolean();
    }
}
