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

@Getter
public class SuspiciousStewEffectsComponent extends DataComponent {

    private List<SuspiciousStewEffect> effects = Collections.emptyList();

    public SuspiciousStewEffectsComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(effects.size(), out);
        for (SuspiciousStewEffect effect : effects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effects = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            SuspiciousStewEffect effect = new SuspiciousStewEffect();
            effect.read(in, protocolId);
            effects.add(effect);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SuspiciousStewEffect implements DataComponentPart {

        private int effectId;
        private int duration;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(effectId, out);
            Packet.writeVarInt(duration, out);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.effectId = Packet.readVarInt(in);
            this.duration = Packet.readVarInt(in);
        }
    }
}
