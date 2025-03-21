package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class WeaponComponent extends DataComponent {

    private int itemDamagePerAttack;
    private float disableBlockingForSeconds;

    public WeaponComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(itemDamagePerAttack, out);
        out.writeFloat(disableBlockingForSeconds);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.itemDamagePerAttack = Packet.readVarInt(in);
        this.disableBlockingForSeconds = in.readFloat();
    }


    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[itemDamagePerAttack=" + itemDamagePerAttack + ",disableBlockingForSeconds=" + disableBlockingForSeconds + "]";
    }
}
