package systems.kinau.fishingbot.event.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.network.utils.Item;

@AllArgsConstructor
public class FishCaughtEvent extends Event {

    @Getter private Item item;
}
