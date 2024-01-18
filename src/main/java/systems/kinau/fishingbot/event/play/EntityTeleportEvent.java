package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
@Getter
public class EntityTeleportEvent extends Event {
    private int entityId;
    private double x;
    private double y;
    private double z;
    private Byte yaw;
    private Byte pitch;
    private boolean onGround;
}
