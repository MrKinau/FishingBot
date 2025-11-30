package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class HolderSetComponentPart implements DataComponentPart {

    private int id;
    private String identifier;
    private List<Integer> ids = Collections.emptyList();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(id, out);
        if (id == 0) {
            Packet.writeString(identifier, out);
        } else {
            for (Integer typeId : ids) {
                Packet.writeVarInt(typeId, out);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.id = Packet.readVarInt(in);
        if (id == 0) {
            this.identifier = Packet.readString(in);
        } else {
            this.ids = new ArrayList<>(id - 1);
            for (int i = 0; i < id - 1; i++) {
                ids.add(Packet.readVarInt(in));
            }
        }
    }
}