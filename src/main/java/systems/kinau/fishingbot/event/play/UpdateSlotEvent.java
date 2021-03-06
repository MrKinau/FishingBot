/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class UpdateSlotEvent extends Event {

    @Getter private int windowId;
    @Getter private short slotId;
    @Getter private Slot slot;

}
