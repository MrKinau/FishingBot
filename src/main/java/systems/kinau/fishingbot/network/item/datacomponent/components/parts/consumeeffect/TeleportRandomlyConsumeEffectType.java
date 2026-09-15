package systems.kinau.fishingbot.network.item.datacomponent.components.parts.consumeeffect;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class TeleportRandomlyConsumeEffectType implements DataComponentPart {

    private float diameter;
    private boolean directionalParticles;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(diameter);
        if (protocolId >= ProtocolConstants.MC_26_3) {
            out.writeBoolean(directionalParticles);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.diameter = in.readFloat();
        if (protocolId >= ProtocolConstants.MC_26_3) {
            this.directionalParticles = in.readBoolean();
        }
    }
}