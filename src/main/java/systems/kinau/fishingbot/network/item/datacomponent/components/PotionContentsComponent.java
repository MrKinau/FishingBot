package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.Effect;
import systems.kinau.fishingbot.network.protocol.Packet;
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
}
