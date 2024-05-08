/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SpawnMobEvent extends Event {

    private int eID;
    private UUID uuid;
    private int type;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private byte headPitch;
    private short velocityX;
    private short velocityY;
    private short velocityZ;
}
