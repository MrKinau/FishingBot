package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.tool.Rule;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
}
