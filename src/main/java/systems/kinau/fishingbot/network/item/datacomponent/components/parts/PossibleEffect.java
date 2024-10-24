package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class PossibleEffect implements DataComponentPart {

    private Effect effect;
    private float probability;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        effect.write(out, protocolId);
        out.writeFloat(probability);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effect = new Effect();
        effect.read(in, protocolId);
        this.probability = in.readFloat();
    }
}