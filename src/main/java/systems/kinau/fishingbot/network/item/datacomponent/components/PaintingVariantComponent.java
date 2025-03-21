package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.Optional;

@Getter
public class PaintingVariantComponent extends DataComponent {

    private int paintingId;

    private int width;
    private int height;
    private String assetId;
    private Optional<NBTTag> optTitle;
    private Optional<NBTTag> optAuthor;

    public PaintingVariantComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(paintingId, out);
        if (paintingId == 0) {
            Packet.writeVarInt(width, out);
            Packet.writeVarInt(height, out);
            Packet.writeString(assetId, out);
            out.writeBoolean(optTitle.isPresent());
            optTitle.ifPresent(nbtTag -> Packet.writeNBT(nbtTag, out));
            out.writeBoolean(optAuthor.isPresent());
            optAuthor.ifPresent(nbtTag -> Packet.writeNBT(nbtTag, out));
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.paintingId = Packet.readVarInt(in);
        if (paintingId == 0) {
            this.width = Packet.readVarInt(in);
            this.height = Packet.readVarInt(in);
            this.assetId = Packet.readString(in);
            if (in.readBoolean())
                this.optTitle = Optional.of(Packet.readNBT(in, protocolId));
            else
                this.optTitle = Optional.empty();
            if (in.readBoolean())
                this.optAuthor = Optional.of(Packet.readNBT(in, protocolId));
            else
                this.optAuthor = Optional.empty();
        }
    }
}
