package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class ResolvableInt implements DataComponentPart {

    private boolean either;
    private int left;
    private String right;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(either);
        if (either) {
            out.writeInt(left);
        } else {
            Packet.writeString(right, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.either = in.readBoolean();
        if (either) {
            this.left = in.readInt();
        } else {
            this.right = Packet.readString(in);
        }
    }
}
