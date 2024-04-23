package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ToolComponent extends DataComponent {

    private List<Rule> rules = Collections.emptyList();
    private float defaultMiningSpeed;
    private int damagePerBlock;

    public ToolComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(rules.size(), out);
        for (Rule rule : rules) {
            rule.write(out, protocolId);
        }
        out.writeFloat(defaultMiningSpeed);
        Packet.writeVarInt(damagePerBlock, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.rules = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Rule rule = new Rule();
            rule.read(in, protocolId);
            rules.add(rule);
        }
        this.defaultMiningSpeed = in.readFloat();
        this.damagePerBlock = Packet.readVarInt(in);
    }

    @Getter
    @NoArgsConstructor
    public static class Rule implements DataComponentPart {

        private AdventureModeComponent.BlockListOrTag blockListOrTag;
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
            AdventureModeComponent.BlockListOrTag blockListOrTag = new AdventureModeComponent.BlockListOrTag();
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
}
