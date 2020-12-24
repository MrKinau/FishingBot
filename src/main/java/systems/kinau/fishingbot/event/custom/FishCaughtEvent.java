package systems.kinau.fishingbot.event.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class FishCaughtEvent extends Event {

    @Getter private Item item;
}
