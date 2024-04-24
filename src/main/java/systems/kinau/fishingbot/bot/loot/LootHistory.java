package systems.kinau.fishingbot.bot.loot;

import lombok.Getter;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LootHistory {

    @Getter private List<LootItem> items = new ArrayList<>();

    public LootItem registerItem(String name, String displayName, List<Enchantment> enchantments) {
        Optional<LootItem> optItem = items.stream().filter(item -> item.getName().equalsIgnoreCase(name)).findAny();
        if (optItem.isPresent()) {
            optItem.get().setCount(optItem.get().getCount() + 1);
            return optItem.get();
        } else {
            LootItem lootItem = new LootItem(name, displayName, 1, enchantments, new ImagedName(displayName, ImageUtils.getFileName(name, enchantments)));
            items.add(lootItem);
            return lootItem;
        }
    }

}
