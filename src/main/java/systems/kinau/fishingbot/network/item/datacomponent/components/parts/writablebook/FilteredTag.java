package systems.kinau.fishingbot.network.item.datacomponent.components.parts.writablebook;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class FilteredTag implements DataComponentPart {

    private NBTTag raw;
    private Optional<NBTTag> filtered;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeNBT(raw, out);
        out.writeBoolean(filtered.isPresent());
        filtered.ifPresent(s -> Packet.writeNBT(s, out));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.raw = Packet.readNBT(in, protocolId);
        if (in.readBoolean())
            filtered = Optional.of(Packet.readNBT(in, protocolId));
        else
            filtered = Optional.empty();
    }
}