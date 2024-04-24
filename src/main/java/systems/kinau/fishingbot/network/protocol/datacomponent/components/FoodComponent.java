package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FoodComponent extends DataComponent {

    private int nutrition;
    private float saturation;
    private boolean canAlwaysEat;
    private float eatSeconds;
    private List<PossibleEffect> possibleEffects = Collections.emptyList();

    public FoodComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(nutrition, out);
        out.writeFloat(saturation);
        out.writeBoolean(canAlwaysEat);
        out.writeFloat(eatSeconds);
        Packet.writeVarInt(possibleEffects.size(), out);
        for (PossibleEffect effect : possibleEffects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.nutrition = Packet.readVarInt(in);
        this.saturation = in.readFloat();
        this.canAlwaysEat = in.readBoolean();
        this.eatSeconds = in.readFloat();
        this.possibleEffects = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            PossibleEffect possibleEffect = new PossibleEffect();
            possibleEffect.read(in, protocolId);
            possibleEffects.add(possibleEffect);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PossibleEffect implements DataComponentPart {

        private PotionContentsComponent.Effect effect;
        private float probability;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            effect.write(out, protocolId);
            out.writeFloat(probability);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.effect = new PotionContentsComponent.Effect();
            effect.read(in, protocolId);
            this.probability = in.readFloat();
        }
    }
}
