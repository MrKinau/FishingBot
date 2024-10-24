package systems.kinau.fishingbot.network.item.datacomponent.components.parts.profile;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Properties implements DataComponentPart {

    private List<Property> properties = Collections.emptyList();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(properties.size(), out);
        for (Property property : properties) {
            property.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.properties = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Property property = new Property();
            property.read(in, protocolId);
            this.properties.add(property);
        }
    }
}