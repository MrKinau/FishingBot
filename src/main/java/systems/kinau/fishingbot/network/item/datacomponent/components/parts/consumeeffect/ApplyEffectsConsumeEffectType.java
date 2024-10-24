package systems.kinau.fishingbot.network.item.datacomponent.components.parts.consumeeffect;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.PossibleEffect;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ApplyEffectsConsumeEffectType implements DataComponentPart {

        private List<PossibleEffect> possibleEffects = Collections.emptyList();

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(possibleEffects.size(), out);
            for (PossibleEffect effect : possibleEffects) {
                effect.write(out, protocolId);
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.possibleEffects = new LinkedList<>();
            int count = Packet.readVarInt(in);
            for (int i = 0; i < count; i++) {
                PossibleEffect possibleEffect = new PossibleEffect();
                possibleEffect.read(in, protocolId);
                possibleEffects.add(possibleEffect);
            }
        }
    }