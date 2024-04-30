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
public class PosLookChangeEvent extends Event {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private  int teleportId;

}
