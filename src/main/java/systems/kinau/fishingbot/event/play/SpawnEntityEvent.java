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
public class SpawnEntityEvent extends Event {

    private int id;
    private int type;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private int objectData;
    private short xVelocity;
    private short yVelocity;
    private short zVelocity;
}
