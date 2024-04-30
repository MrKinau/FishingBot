package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class DyedColorComponent extends DataComponent {

    private int color;
    private boolean showInTooltip;

    public DyedColorComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeInt(color);
        out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.color = in.readInt();
        this.showInTooltip = in.readBoolean();
    }
}
