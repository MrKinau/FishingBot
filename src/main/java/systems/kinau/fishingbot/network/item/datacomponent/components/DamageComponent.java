package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class DamageComponent extends DataComponent {

    private int damage = -1;

    public DamageComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(damage, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.damage = Packet.readVarInt(in);
    }
}
