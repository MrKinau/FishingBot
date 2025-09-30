package systems.kinau.fishingbot.network.item.datacomponent.components.parts;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

public class TypedEntityData implements DataComponentPart {

    private int entityType;
    private NBTTag entityData;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId >= ProtocolConstants.MC_1_21_9)
            Packet.writeVarInt(entityType, out);
        Packet.writeNBT(entityData, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (protocolId >= ProtocolConstants.MC_1_21_9)
            this.entityType = Packet.readVarInt(in);
        this.entityData = Packet.readNBT(in, protocolId);
    }
}
