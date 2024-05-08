package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.HashMap;
import java.util.Map;

public class BlockStateComponent extends DataComponent {

    private Map<String, String> properties = new HashMap<>();

    public BlockStateComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(properties.size(), out);
        properties.forEach((key, value) -> {
            Packet.writeString(key, out);
            Packet.writeString(value, out);
        });
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.properties = new HashMap<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            String key = Packet.readString(in);
            String value = Packet.readString(in);
            properties.put(key, value);
        }
    }
}
