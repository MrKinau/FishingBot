package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class LodestoneTrackerComponent extends DataComponent {

    private Optional<GlobalPos> target;
    private boolean tracked;

    public LodestoneTrackerComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(target.isPresent());
        target.ifPresent(globalPos -> globalPos.write(out, protocolId));
        out.writeBoolean(tracked);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (in.readBoolean()) {
            GlobalPos globalPos = new GlobalPos();
            globalPos.read(in, protocolId);
            this.target = Optional.of(globalPos);
        } else {
            this.target = Optional.empty();
        }
        this.tracked = in.readBoolean();
    }

    @Getter
    @NoArgsConstructor
    public static class GlobalPos implements DataComponentPart {

        private String dimension;
        private BlockPos pos;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeString(dimension, out);
            pos.write(out, protocolId);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.dimension = Packet.readString(in);
            BlockPos pos = new BlockPos();
            pos.read(in, protocolId);
            this.pos = pos;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BlockPos implements DataComponentPart {

        private long pos;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            out.writeLong(pos);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.pos = in.readLong();
        }
    }
}
