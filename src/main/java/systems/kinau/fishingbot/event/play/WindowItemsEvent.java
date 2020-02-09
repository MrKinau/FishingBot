package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.ItemStack;
import systems.kinau.fishingbot.event.Event;

import java.util.Map;

@AllArgsConstructor
public class WindowItemsEvent extends Event {

    @Getter private int windowId;
    @Getter private Map<Integer, ItemStack> slotData;

}
