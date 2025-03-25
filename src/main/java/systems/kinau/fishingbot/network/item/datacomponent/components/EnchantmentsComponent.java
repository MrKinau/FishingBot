package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EnchantmentsComponent extends DataComponent {

    private List<Enchantment> enchantments;
    private boolean showInTooltip = true;

    public EnchantmentsComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(enchantments.size(), out);
        for (Enchantment enchantment : enchantments) {
            enchantment.write(out, protocolId);
        }
        if (protocolId < ProtocolConstants.MC_1_21_5)
            out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.enchantments = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            Enchantment enchantment = new Enchantment();
            enchantment.read(in, protocolId);
            enchantments.add(enchantment);
        }
        if (protocolId < ProtocolConstants.MC_1_21_5)
            this.showInTooltip = in.readBoolean();
    }

    @Override
    public String toString(int protocolId) {
        return super.toString(protocolId) + "[enchantments=[" + enchantments.stream().map(enchantment -> enchantment.toString(protocolId)).collect(Collectors.joining(",")) + "],showInTooltip=" + showInTooltip + "]";
    }
}
