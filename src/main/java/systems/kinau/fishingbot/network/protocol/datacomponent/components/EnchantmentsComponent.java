package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.LinkedList;
import java.util.List;

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
        this.showInTooltip = in.readBoolean();
    }
}
