package systems.kinau.fishingbot.network.item.datacomponent.components.parts.tool;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.BlockListOrTag;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class Rule implements DataComponentPart {

    private BlockListOrTag blockListOrTag;
    private Optional<Float> miningSpeed;
    private Optional<Boolean> correctForDrops;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        blockListOrTag.write(out, protocolId);
        out.writeBoolean(miningSpeed.isPresent());
        miningSpeed.ifPresent(out::writeFloat);
        out.writeBoolean(correctForDrops.isPresent());
        correctForDrops.ifPresent(out::writeBoolean);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        BlockListOrTag blockListOrTag = new BlockListOrTag();
        blockListOrTag.read(in, protocolId);
        this.blockListOrTag = blockListOrTag;

        if (in.readBoolean())
            miningSpeed = Optional.of(in.readFloat());
        else
            miningSpeed = Optional.empty();

        if (in.readBoolean())
            correctForDrops = Optional.of(in.readBoolean());
        else
            correctForDrops = Optional.empty();
    }
}