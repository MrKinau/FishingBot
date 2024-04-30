package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
        this.showInTooltip = in.readBoolean();
    }

    @Getter
    @NoArgsConstructor
    public static class BlockPredicate implements DataComponentPart {
        private Optional<BlockListOrTag> blockPredicate;
        private Optional<List<BlockState>> statePredicate;
        private Optional<NBTTag> nbtPredicate;

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
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BlockListOrTag implements DataComponentPart {

        private String tag;
        private List<Integer> blockIds = Collections.emptyList();

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            if (tag == null) {
                Packet.writeVarInt(blockIds.size() + 1, out);
                for (Integer blockId : blockIds) {
                    Packet.writeVarInt(blockId, out);
                }
            } else {
                Packet.writeVarInt(0, out);
                Packet.writeString(tag, out);
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.blockIds = new LinkedList<>();
            this.tag = null;

            int i = Packet.readVarInt(in) - 1;
            if (i == -1) {
                this.tag = Packet.readString(in);
            } else {
                for (int j = 0; j < i; j++) {
                    blockIds.add(Packet.readVarInt(in));
                }
            }
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BlockState implements DataComponentPart {

        private String name;
        private String exactState;
        private String minState;
        private String maxState;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeString(name, out);
            out.writeBoolean(exactState != null);
            if (exactState != null) {
                Packet.writeString(exactState, out);
            } else {
                out.writeBoolean(minState != null);
                if (minState != null)
                    Packet.writeString(minState, out);
                out.writeBoolean(maxState != null);
                if (maxState != null)
                    Packet.writeString(maxState, out);
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.exactState = null;
            this.minState = null;
            this.maxState = null;

            this.name = Packet.readString(in);
            if (in.readBoolean()) {
                this.exactState = Packet.readString(in);
            } else {
                if (in.readBoolean())
                    minState = Packet.readString(in);
                if (in.readBoolean())
                    maxState = Packet.readString(in);
            }
        }
    }
}
