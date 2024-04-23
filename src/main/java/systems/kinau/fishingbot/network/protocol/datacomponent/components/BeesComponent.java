package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class BeesComponent extends DataComponent {

    private List<Bee> bees = Collections.emptyList();

    public BeesComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(bees.size(), out);
        for (Bee bee : bees) {
            bee.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.bees = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Bee bee = new Bee();
            bee.read(in, protocolId);
            bees.add(bee);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Bee implements DataComponentPart {
        private NBTTag entityData;
        private int ticksInHive;
        private int minTicksInHive;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeNBT(entityData, out);
            Packet.writeVarInt(ticksInHive, out);
            Packet.writeVarInt(minTicksInHive, out);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.entityData = Packet.readNBT(in, protocolId);
            this.ticksInHive = Packet.readVarInt(in);
            this.minTicksInHive = Packet.readVarInt(in);
        }
    }
}
