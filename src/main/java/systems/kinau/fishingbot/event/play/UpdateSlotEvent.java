/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class UpdateSlotEvent extends Event {

    private int windowId;
    private short slotId;
    private Slot slot;

}
