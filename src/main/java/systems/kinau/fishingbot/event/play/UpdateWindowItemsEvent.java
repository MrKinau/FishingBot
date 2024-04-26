/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.Event;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateWindowItemsEvent extends Event {

    private int windowId;
    private List<Slot> slots;

}
