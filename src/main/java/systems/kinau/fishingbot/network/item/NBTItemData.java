package systems.kinau.fishingbot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.utils.nbt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NBTItemData implements ItemData {

    private final NBTTag nbtData;

    @Override
    public List<Enchantment> getEnchantments() {
        List<Enchantment> enchantmentList = new ArrayList<>();
        Tag<?> rootTag = nbtData.getTag();
        if (!(rootTag instanceof CompoundTag)) return enchantmentList;
        CompoundTag root = (CompoundTag) rootTag;
        String key;
        if (root.containsKey("StoredEnchantments"))
            key = "StoredEnchantments";
        else if (root.containsKey("ench"))
            key = "ench";
        else if (root.containsKey("Enchantments"))
            key = "Enchantments";
        else
            return enchantmentList;
        List<CompoundTag> enchants = root.get(key, ListTag.class).getValue().stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(tag -> (CompoundTag) tag)
                .collect(Collectors.toList());
        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();
        for (CompoundTag enchant : enchants) {
            String enchType;
            short level = enchant.get("lvl", ShortTag.class).getValue();
            if (protocolId >= ProtocolConstants.MC_1_13) {
                enchType = enchant.get("id", StringTag.class).getValue();
            } else {
                short id = enchant.get("id", ShortTag.class).getValue();
                enchType = Registries.ENCHANTMENT.getEnchantmentName(id, protocolId);
            }
            enchantmentList.add(new Enchantment(enchType, level));
        }
        return enchantmentList;
    }

    @Override
    public void write(ByteArrayDataOutput output, int protocolId) {
        Packet.writeNBT(nbtData, output);
    }
}
