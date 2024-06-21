package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentRegistry;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class FoodComponent extends DataComponent {

    private final DataComponentRegistry dataComponentRegistry;

    private int nutrition;
    private float saturation;
    private boolean canAlwaysEat;
    private float eatSeconds;
    private Optional<Slot> usingConvertsTo = Optional.empty();
    private List<PossibleEffect> possibleEffects = Collections.emptyList();

    public FoodComponent(DataComponentRegistry dataComponentRegistry, int componentTypeId) {
        super(componentTypeId);
        this.dataComponentRegistry = dataComponentRegistry;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(nutrition, out);
        out.writeFloat(saturation);
        out.writeBoolean(canAlwaysEat);
        out.writeFloat(eatSeconds);
        if (protocolId >= ProtocolConstants.MC_1_21) {
            out.writeBoolean(usingConvertsTo.isPresent());
            usingConvertsTo.ifPresent(slot -> Packet.writeSlot(slot, out, protocolId));
        }
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
        if (protocolId >= ProtocolConstants.MC_1_21) {
            if (in.readBoolean())
                this.usingConvertsTo = Optional.of(Packet.readSlot(in, protocolId, dataComponentRegistry));
            else
                this.usingConvertsTo = Optional.empty();
        }
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
