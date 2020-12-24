package systems.kinau.fishingbot.bot;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Inventory {

    private Map<Integer, Slot> content = new HashMap<>();
    private short actionCounter = 1;
    private int windowId;

    public void setItem(int slotId, Slot slot) {
        this.content.put(slotId, slot);
    }
}
