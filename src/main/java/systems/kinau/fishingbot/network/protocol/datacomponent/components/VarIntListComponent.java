package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VarIntListComponent extends DataComponent {

    private List<Integer> values = Collections.emptyList();

    public VarIntListComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(values.size(), out);
        for (Integer value : values) {
            Packet.writeVarInt(value, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.values = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            this.values.add(Packet.readVarInt(in));
        }
    }
}
