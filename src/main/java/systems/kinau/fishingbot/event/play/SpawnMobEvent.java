/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.UUID;

@AllArgsConstructor
public class SpawnMobEvent extends Event {

    @Getter private int eID;
    @Getter private UUID uuid;
    @Getter private int type;
    @Getter private double x;
    @Getter private double y;
    @Getter private double z;
    @Getter private byte yaw;
    @Getter private byte pitch;
    @Getter private byte headPitch;
    @Getter private short velocityX;
    @Getter private short velocityY;
    @Getter private short velocityZ;
}
