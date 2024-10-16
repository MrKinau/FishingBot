package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathProtectionComponent extends DataComponent {

    private List<ConsumableComponent.ConsumeEffect> deathEffects = Collections.emptyList();

    public DeathProtectionComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(deathEffects.size(), out);
        for (ConsumableComponent.ConsumeEffect effect : deathEffects) {
            effect.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        int count = Packet.readVarInt(in);
        this.deathEffects = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ConsumableComponent.ConsumeEffect consumeEffect = new ConsumableComponent.ConsumeEffect();
            consumeEffect.read(in, protocolId);
            deathEffects.add(consumeEffect);
        }
    }
}
