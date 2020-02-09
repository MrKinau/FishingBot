/*
 * Created by David Luedtke (MrKinau)
 * 2020/2/8
 */

package systems.kinau.fishingbot.bot;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;

public class Inventory {

    @Getter
    private ItemStack[] content = new ItemStack[45];

    public void setItem(int slotId, ItemStack item) {
        if(item != null && item.getMaterial() != null)
            MineBot.getLog().info("SET " + slotId + " = " + item.getMaterial().name());
        if (slotId < content.length)
            content[slotId] = item;
    }

    public ItemStack getSlot(int slotId) {
        return content[slotId];
    }

    public boolean isHotbarEmpty() {
        for (int i = 36; i <= 44; i++) {
            if (getSlot(i) != null || getSlot(i).getItemId() > 0)
                return true;
        }
        return false;
    }
}
