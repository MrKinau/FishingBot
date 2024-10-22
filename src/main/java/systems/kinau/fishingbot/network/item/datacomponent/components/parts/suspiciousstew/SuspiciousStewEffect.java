package systems.kinau.fishingbot.network.item.datacomponent.components.parts.suspiciousstew;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class SuspiciousStewEffect implements DataComponentPart {

    private int effectId;
    private int duration;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(effectId, out);
        Packet.writeVarInt(duration, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.effectId = Packet.readVarInt(in);
        this.duration = Packet.readVarInt(in);
    }
}