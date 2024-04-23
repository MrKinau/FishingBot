package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LoreComponent extends DataComponent {

    private List<NBTTag> lore = Collections.emptyList();

    public LoreComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(lore.size(), out);
        for (NBTTag nbtTag : lore) {
            Packet.writeNBT(nbtTag, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.lore = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            NBTTag line = Packet.readNBT(in, protocolId);
            lore.add(line);
        }
    }
}
