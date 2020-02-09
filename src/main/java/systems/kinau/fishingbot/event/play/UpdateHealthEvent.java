/*
 * Created by David Luedtke (MrKinau)
 * 2020/2/8
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class UpdateHealthEvent extends Event {

    @Getter private float health;
    @Getter private int food;
    @Getter private float saturation;
}
