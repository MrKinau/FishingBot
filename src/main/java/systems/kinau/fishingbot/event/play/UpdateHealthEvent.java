package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class UpdateHealthEvent extends Event {

    private int eid;
    private float health;
    private int food;
    private float saturation;

}
