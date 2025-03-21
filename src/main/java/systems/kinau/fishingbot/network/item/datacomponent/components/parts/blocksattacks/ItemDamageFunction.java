package systems.kinau.fishingbot.network.item.datacomponent.components.parts.blocksattacks;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class ItemDamageFunction implements DataComponentPart {

    private float threshold;
    private float base;
    private float factor;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(threshold);
        out.writeFloat(base);
        out.writeFloat(factor);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.threshold = in.readFloat();
        this.base = in.readFloat();
        this.factor = in.readFloat();
    }
}
