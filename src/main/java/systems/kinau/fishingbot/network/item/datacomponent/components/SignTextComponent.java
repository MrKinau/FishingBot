package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SignTextComponent extends DataComponent {

    private List<NBTTag> lines;
    private Optional<List<NBTTag>> filteredLines;
    private int color;
    private boolean glowing;

    public SignTextComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(lines.size(), out);
        for (NBTTag line : lines) {
            Packet.writeNBT(line, out);
        }

        out.writeBoolean(filteredLines.isPresent());
        if (filteredLines.isPresent()) {
            Packet.writeVarInt(filteredLines.get().size(), out);
            for (NBTTag line : filteredLines.get()) {
                Packet.writeNBT(line, out);
            }
        }

        Packet.writeVarInt(color, out);
        out.writeBoolean(glowing);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.lines = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            lines.add(Packet.readNBT(in, protocolId));
        }

        if (in.readBoolean()) {
            List<NBTTag> actualFilteredLines = new ArrayList<>();
            this.filteredLines = Optional.of(actualFilteredLines);
            for (int i = 0; i < 4; i++) {
                actualFilteredLines.add(Packet.readNBT(in, protocolId));
            }
        } else {
            this.filteredLines = Optional.empty();
        }

        this.color = Packet.readVarInt(in);
        this.glowing = in.readBoolean();
    }
}
