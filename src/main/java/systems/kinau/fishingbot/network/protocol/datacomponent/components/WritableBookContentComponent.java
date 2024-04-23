package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
public class WritableBookContentComponent extends DataComponent {

    private List<FilteredString> value = Collections.emptyList();

    public WritableBookContentComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(value.size(), out);
        for (FilteredString filteredString : value) {
            filteredString.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.value = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            FilteredString filteredString = new FilteredString();
            filteredString.read(in, protocolId);
            this.value.add(filteredString);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class FilteredString implements DataComponentPart {

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
}
