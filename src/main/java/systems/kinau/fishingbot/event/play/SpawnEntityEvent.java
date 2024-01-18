/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class SpawnEntityEvent extends Event {

    @Getter private int id;
    @Getter private int type;
    @Getter private double x;
    @Getter private double y;
    @Getter private double z;
    @Getter private byte yaw;
    @Getter private byte pitch;
    @Getter private int objectData;
    @Getter private short xVelocity;
    @Getter private short yVelocity;
    @Getter private short zVelocity;
}
