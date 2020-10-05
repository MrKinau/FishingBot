package systems.kinau.fishingbot.bot;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Inventory {

    private Map<Integer, Slot> content = new HashMap<>();

    public void setItem(int slotId, Slot slot) {
        content.put(slotId, slot);
    }
}
