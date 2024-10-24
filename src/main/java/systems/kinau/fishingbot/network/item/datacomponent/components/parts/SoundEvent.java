package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class SoundEvent implements DataComponentPart {

    private int soundEventId;
    private String resourceLocation;
    private Optional<Float> fixedRange;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(soundEventId, out);
        if (soundEventId == 0) {
            Packet.writeString(resourceLocation, out);
            if (fixedRange.isPresent())
                out.writeFloat(fixedRange.get());
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.soundEventId = Packet.readVarInt(in);
        if (soundEventId == 0) {
            this.resourceLocation = Packet.readString(in);
            if (in.readBoolean())
                fixedRange = Optional.of(in.readFloat());
            else
                fixedRange = Optional.empty();
        }
    }

    @Override
    public String toString(int protocolId) {
        if (soundEventId != 0)
            return "{soundEventId=" + soundEventId + "}";
        return "{resourceLocation=" + resourceLocation + (fixedRange.isPresent() ? ",fixedRange=" + fixedRange : "") + "}";
    }
}