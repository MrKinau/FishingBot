package systems.kinau.fishingbot.network.item.datacomponent.components.parts.blocksattacks;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.HolderSetComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class DamageReduction implements DataComponentPart {

    private float horizontalBlockingAngle;
    private Optional<HolderSetComponentPart> optDamageTypes = Optional.empty();
    private float baseReduction;
    private float reductionFactor;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(horizontalBlockingAngle);
        out.writeBoolean(optDamageTypes.isPresent());
        if (optDamageTypes.isPresent()) {
            optDamageTypes.get().write(out, protocolId);
        }
        out.writeFloat(baseReduction);
        out.writeFloat(reductionFactor);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.horizontalBlockingAngle = in.readFloat();
        if (in.readBoolean()) {
            HolderSetComponentPart damageTypes = new HolderSetComponentPart();
            damageTypes.read(in, protocolId);
            this.optDamageTypes = Optional.of(damageTypes);
        } else {
            this.optDamageTypes = Optional.empty();
        }
        this.baseReduction = in.readFloat();
        this.reductionFactor = in.readFloat();
    }
}
