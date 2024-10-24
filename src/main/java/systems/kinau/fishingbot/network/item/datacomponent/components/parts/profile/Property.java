package systems.kinau.fishingbot.network.item.datacomponent.components.parts.profile;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class Property implements DataComponentPart {

    private String name;
    private String value;
    private String signature;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeString(name, out);
        Packet.writeString(value, out);
        out.writeBoolean(signature != null);
        if (signature != null)
            Packet.writeString(signature, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.name = Packet.readString(in);
        this.value = Packet.readString(in);
        this.signature = null;
        if (in.readBoolean())
            signature = Packet.readString(in);
    }
}