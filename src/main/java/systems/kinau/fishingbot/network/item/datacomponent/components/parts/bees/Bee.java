package systems.kinau.fishingbot.network.item.datacomponent.components.parts.bees;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.TypedEntityData;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class Bee implements DataComponentPart {

    private TypedEntityData entityData;
    private int ticksInHive;
    private int minTicksInHive;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        entityData.write(out, protocolId);
        Packet.writeVarInt(ticksInHive, out);
        Packet.writeVarInt(minTicksInHive, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.entityData = new TypedEntityData();
        entityData.read(in, protocolId);
        this.ticksInHive = Packet.readVarInt(in);
        this.minTicksInHive = Packet.readVarInt(in);
    }
}