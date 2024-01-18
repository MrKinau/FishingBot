package systems.kinau.fishingbot.modules.fishing;

import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class PossibleCaughtList extends CopyOnWriteArraySet<Item> {

    public void addCaught(Item item) {
        if (contains(item)) {
            updateCaught(item.getEid(), item.getName(), item.getItemId(), item.getEnchantments(), item.getMotX(), item.getMotY(), item.getMotZ());
            return;
        }
        add(item);
    }

    public void updateCaught(int eId, String name, Integer itemId, List<Enchantment> enchantments, int motX, int motY, int motZ) {
        stream().filter(existing -> existing.getEid() == eId).findAny().ifPresent(existing -> {
            if (existing.getName() == null)
                existing.setName(name);
            if (existing.getItemId() == null)
                existing.setItemId(itemId);
            if (existing.getEnchantments() == null)
                existing.setEnchantments(enchantments);
            if (Item.getMaxMot(motX, motY, motZ) > existing.getMaxMot()) {
                existing.setMotX(motX);
                existing.setMotY(motY);
                existing.setMotZ(motZ);
            }
        });
    }
}