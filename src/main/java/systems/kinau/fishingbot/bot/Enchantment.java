package systems.kinau.fishingbot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Enchantment implements DataComponentPart {

    private int enchantmentId = -1;
    private String enchantmentType;
    private int level;

    public Enchantment(String enchantmentType, int level) {
        this.enchantmentType = enchantmentType;
        this.level = level;
    }

    public String getEnchantmentNameWithoutNamespace() {
        return enchantmentType.replace("minecraft:", "");
    }

    public String getDisplayName() {
        return FishingBot.getInstance().getCurrentBot().getMinecraftTranslations().getEnchantmentName(enchantmentType);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(enchantmentId, out);
        Packet.writeVarInt(level, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.enchantmentId = Packet.readVarInt(in);
        this.level = Packet.readVarInt(in);
        this.enchantmentType = Registries.ENCHANTMENT.getEnchantmentName(enchantmentId, protocolId);
    }

    @Override
    public String toString(int protocolId) {
        return "{enchantment=" + getEnchantmentType() + ",level=" + level + "}";
    }
}
