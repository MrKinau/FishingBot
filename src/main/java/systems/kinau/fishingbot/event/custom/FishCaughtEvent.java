package systems.kinau.fishingbot.event.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.loot.LootItem;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class FishCaughtEvent extends Event {

    private Item item;
    private LootItem lootItem;
}
