package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class EitherVarIntOrIdentifier implements DataComponentPart {

    private boolean either;
    private int damageTypeId;
    private String damageTypeIdentifier;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(either);
        if (either) {
            Packet.writeVarInt(damageTypeId, out);
        } else {
            Packet.writeString(damageTypeIdentifier, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.either = in.readBoolean();
        if (either) {
            this.damageTypeId = Packet.readVarInt(in);
        } else {
            this.damageTypeIdentifier = Packet.readString(in);
        }
    }
}
