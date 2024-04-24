package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class WrittenBookContentComponent extends DataComponent {

    private WritableBookContentComponent.FilteredString title;
    private String author;
    private int generation;
    private List<FilteredTag> pages = Collections.emptyList();
    private boolean resolved;

    public WrittenBookContentComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        title.write(out, protocolId);
        Packet.writeString(author, out);
        Packet.writeVarInt(generation, out);
        Packet.writeVarInt(pages.size(), out);
        for (FilteredTag nbt : pages) {
            nbt.write(out, protocolId);
        }
        out.writeBoolean(resolved);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        WritableBookContentComponent.FilteredString title = new WritableBookContentComponent.FilteredString();
        title.read(in, protocolId);
        this.title = title;

        this.author = Packet.readString(in);
        this.generation = Packet.readVarInt(in);
        this.pages = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            FilteredTag tag = new FilteredTag();
            tag.read(in, protocolId);
            pages.add(tag);
        }
        this.resolved = in.readBoolean();
    }

    @Getter
    @NoArgsConstructor
    public static class FilteredTag implements DataComponentPart {

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
}
