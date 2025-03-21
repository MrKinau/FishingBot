package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TooltipDisplayComponent extends DataComponent {

    private boolean hideTooltip;
    private List<Integer> hiddenComponents = new ArrayList<>();

    public TooltipDisplayComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(hideTooltip);
        Packet.writeVarInt(hiddenComponents.size(), out);
        for (Integer hiddenComponent : hiddenComponents) {
            Packet.writeVarInt(hiddenComponent, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.hideTooltip = in.readBoolean();
        int count = Packet.readVarInt(in);
        this.hiddenComponents = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            hiddenComponents.add(Packet.readVarInt(in));
        }
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[hideTooltip=" + hideTooltip + ",hiddenComponents=" + hiddenComponents + "]";
    }
}
