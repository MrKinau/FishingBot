package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

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
public class BlockListOrTag implements DataComponentPart {

    private String tag;
    private List<Integer> blockIds = Collections.emptyList();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (tag == null) {
            Packet.writeVarInt(blockIds.size() + 1, out);
            for (Integer blockId : blockIds) {
                Packet.writeVarInt(blockId, out);
            }
        } else {
            Packet.writeVarInt(0, out);
            Packet.writeString(tag, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.blockIds = new LinkedList<>();
        this.tag = null;

        int i = Packet.readVarInt(in) - 1;
        if (i == -1) {
            this.tag = Packet.readString(in);
        } else {
            for (int j = 0; j < i; j++) {
                blockIds.add(Packet.readVarInt(in));
            }
        }
    }
}