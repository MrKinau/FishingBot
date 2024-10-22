package systems.kinau.fishingbot.network.item.datacomponent.components.parts.lodestone;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class GlobalPos implements DataComponentPart {

    private String dimension;
    private BlockPos pos;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeString(dimension, out);
        pos.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.dimension = Packet.readString(in);
        BlockPos pos = new BlockPos();
        pos.read(in, protocolId);
        this.pos = pos;
    }
}