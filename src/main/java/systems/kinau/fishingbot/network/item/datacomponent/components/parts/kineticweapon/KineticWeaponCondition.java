package systems.kinau.fishingbot.network.item.datacomponent.components.parts.kineticweapon;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class KineticWeaponCondition implements DataComponentPart {

    private int maxDurationTicks;
    private float minSpeed;
    private float minRelativeSpeed;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(maxDurationTicks, out);
        out.writeFloat(minSpeed);
        out.writeFloat(minRelativeSpeed);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.maxDurationTicks = Packet.readVarInt(in);
        this.minSpeed = in.readFloat();
        this.minRelativeSpeed = in.readFloat();
    }
}
