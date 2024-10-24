package systems.kinau.fishingbot.network.item.datacomponent.components.parts.lodestone;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class BlockPos implements DataComponentPart {

    private long pos;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeLong(pos);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.pos = in.readLong();
    }
}