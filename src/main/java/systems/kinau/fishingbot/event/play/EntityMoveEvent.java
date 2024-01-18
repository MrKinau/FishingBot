package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
@Getter
public class EntityMoveEvent extends Event {
    private int entityId;
    private short dX;
    private short dY;
    private short dZ;
    private Byte yaw;
    private Byte pitch;
    private boolean onGround;
}
