package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class IntComponent extends DataComponent {

    private int value;

    public IntComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeInt(value);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.value = in.readInt();
    }
}
