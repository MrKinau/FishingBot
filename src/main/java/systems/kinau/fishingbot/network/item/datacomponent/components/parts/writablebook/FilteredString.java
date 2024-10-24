package systems.kinau.fishingbot.network.item.datacomponent.components.parts.writablebook;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class FilteredString implements DataComponentPart {

    private String raw;
    private Optional<String> filtered;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeString(raw, out);
        out.writeBoolean(filtered.isPresent());
        filtered.ifPresent(s -> Packet.writeString(s, out));
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.raw = Packet.readString(in);
        if (in.readBoolean())
            filtered = Optional.of(Packet.readString(in));
        else
            filtered = Optional.empty();
    }
}