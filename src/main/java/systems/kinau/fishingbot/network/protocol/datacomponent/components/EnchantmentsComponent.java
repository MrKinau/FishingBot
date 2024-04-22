package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.LinkedList;
import java.util.List;

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
            Packet.writeVarInt(Registries.ENCHANTMENT.findKey(enchantment.getEnchantmentType(), protocolId), out);
            Packet.writeVarInt(enchantment.getLevel(), out);
        }
        out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.enchantments = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            int enchantmentId = Packet.readVarInt(in);
            int level = Packet.readVarInt(in);
            enchantments.add(new Enchantment(Registries.ENCHANTMENT.getEnchantmentName(enchantmentId, protocolId), level));
        }
        this.showInTooltip = in.readBoolean();
    }
}
