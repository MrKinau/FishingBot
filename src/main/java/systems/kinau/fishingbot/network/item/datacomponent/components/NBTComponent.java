package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@Getter
public class NBTComponent extends DataComponent {

    private NBTTag tag;

    public NBTComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeNBT(tag, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.tag = Packet.readNBT(in, protocolId);
    }
}
