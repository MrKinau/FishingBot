package systems.kinau.fishingbot.network.item.datacomponent.components.parts.adventuremode;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.BlockListOrTag;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class BlockPredicate implements DataComponentPart {

    private Optional<BlockListOrTag> blockPredicate;
    private Optional<List<BlockState>> statePredicate;
    private Optional<NBTTag> nbtPredicate;
    private DataComponentMatcher dataComponentMatcher;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(blockPredicate.isPresent());
        blockPredicate.ifPresent(blockListOrTag -> blockListOrTag.write(out, protocolId));

        out.writeBoolean(statePredicate.isPresent());
        statePredicate.ifPresent(blockStates -> {
            Packet.writeVarInt(blockStates.size(), out);
            blockStates.forEach(blockState -> blockState.write(out, protocolId));
        });

        out.writeBoolean(nbtPredicate.isPresent());
        nbtPredicate.ifPresent(nbt -> Packet.writeNBT(nbt, out));

        if (protocolId >= ProtocolConstants.MC_1_21_5) {
            dataComponentMatcher.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (in.readBoolean()) {
            BlockListOrTag blocks = new BlockListOrTag();
            blocks.read(in, protocolId);
            this.blockPredicate = Optional.of(blocks);
        } else {
            this.blockPredicate = Optional.empty();
        }

        if (in.readBoolean()) {
            int count = Packet.readVarInt(in);
            List<BlockState> states = new LinkedList<>();
            for (int i = 0; i < count; i++) {
                BlockState state = new BlockState();
                state.read(in, protocolId);
                states.add(state);
            }
            this.statePredicate = Optional.of(states);
        } else {
            this.statePredicate = Optional.empty();
        }

        if (in.readBoolean()) {
            this.nbtPredicate = Optional.of(Packet.readNBT(in, protocolId));
        } else {
            this.nbtPredicate = Optional.empty();
        }

        if (protocolId >= ProtocolConstants.MC_1_21_5) {
            this.dataComponentMatcher = new DataComponentMatcher();
            dataComponentMatcher.read(in, protocolId);
        }
    }
}