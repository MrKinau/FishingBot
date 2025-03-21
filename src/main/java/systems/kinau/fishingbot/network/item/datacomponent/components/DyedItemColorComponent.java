package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class DyedItemColorComponent extends DataComponent {

    private int color;
    private boolean showInTooltip;

    public DyedItemColorComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeInt(color);
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.color = in.readInt();
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1)
            this.showInTooltip = in.readBoolean();
    }
}
