package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class EffectDetails implements DataComponentPart {

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