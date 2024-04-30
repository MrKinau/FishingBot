/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class EntityVelocityEvent extends Event {

    private short x;
    private short y;
    private short z;
    private int eid;
}
