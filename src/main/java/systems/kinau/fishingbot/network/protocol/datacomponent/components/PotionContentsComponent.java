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
import java.util.Optional;

public class PotionContentsComponent extends DataComponent {

    private Optional<Integer> potionId;
    private Optional<Integer> customColor;
    private List<Effect> effects = Collections.emptyList();

    public PotionContentsComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(potionId.isPresent());
        potionId.ifPresent(id -> Packet.writeVarInt(id, out));
        out.writeBoolean(customColor.isPresent());
        customColor.ifPresent(out::writeInt);
        Packet.writeVarInt(effects.size(), out);
        for (Effect effect : effects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (in.readBoolean()) {
            potionId = Optional.of(Packet.readVarInt(in));
        } else {
            potionId = Optional.empty();
        }

        if (in.readBoolean()) {
            customColor = Optional.of(in.readInt());
        } else {
            customColor = Optional.empty();
        }

        this.effects = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Effect effect = new Effect();
            effect.read(in, protocolId);
            effects.add(effect);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Effect implements DataComponentPart {

        private int effectId;
        private EffectDetails details;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(effectId, out);
            details.write(out, protocolId);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.effectId = Packet.readVarInt(in);
            EffectDetails details = new EffectDetails();
            details.read(in, protocolId);
            this.details = details;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class EffectDetails implements DataComponentPart {

        private int amplifier;
        private int duration;
        private boolean ambient;
        private boolean showParticle;
        private boolean showIcon;
        private Optional<EffectDetails> hiddenEffect;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(amplifier, out);
            Packet.writeVarInt(duration, out);
            out.writeBoolean(ambient);
            out.writeBoolean(showParticle);
            out.writeBoolean(showIcon);
            out.writeBoolean(hiddenEffect.isPresent());
            hiddenEffect.ifPresent(effect -> effect.write(out, protocolId));
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.amplifier = Packet.readVarInt(in);
            this.duration = Packet.readVarInt(in);
            this.ambient = in.readBoolean();
            this.showParticle = in.readBoolean();
            this.showIcon = in.readBoolean();
            if (in.readBoolean()) {
                EffectDetails hiddenEffect = new EffectDetails();
                hiddenEffect.read(in, protocolId);
                this.hiddenEffect = Optional.of(hiddenEffect);
            }
        }
    }
}
