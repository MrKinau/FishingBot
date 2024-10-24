package systems.kinau.fishingbot.network.item.datacomponent.components.parts.adventuremode;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class BlockState implements DataComponentPart {

    private String name;
    private String exactState;
    private String minState;
    private String maxState;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeString(name, out);
        out.writeBoolean(exactState != null);
        if (exactState != null) {
            Packet.writeString(exactState, out);
        } else {
            out.writeBoolean(minState != null);
            if (minState != null)
                Packet.writeString(minState, out);
            out.writeBoolean(maxState != null);
            if (maxState != null)
                Packet.writeString(maxState, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.exactState = null;
        this.minState = null;
        this.maxState = null;

        this.name = Packet.readString(in);
        if (in.readBoolean()) {
            this.exactState = Packet.readString(in);
        } else {
            if (in.readBoolean())
                minState = Packet.readString(in);
            if (in.readBoolean())
                maxState = Packet.readString(in);
        }
    }
}