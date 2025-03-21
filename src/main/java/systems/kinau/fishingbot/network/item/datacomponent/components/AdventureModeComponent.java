package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.adventuremode.BlockPredicate;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AdventureModeComponent extends DataComponent {

    private List<BlockPredicate> blockPredicates = Collections.emptyList();
    private boolean showInTooltip;

    public AdventureModeComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(blockPredicates.size(), out);
        for (BlockPredicate blockPredicate : blockPredicates) {
            blockPredicate.write(out, protocolId);
        }
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.blockPredicates = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            BlockPredicate predicate = new BlockPredicate();
            predicate.read(in, protocolId);
            blockPredicates.add(predicate);
        }
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1)
            this.showInTooltip = in.readBoolean();
    }
}
