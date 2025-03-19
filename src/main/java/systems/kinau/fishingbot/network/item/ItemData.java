package systems.kinau.fishingbot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.bot.Enchantment;

import java.util.List;

public interface ItemData {

    List<Enchantment> getEnchantments();

    void write(ByteArrayDataOutput output, int protocolId);

    default void writeHashes(ByteArrayDataOutput output, int protocolId) {}
}
