package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.network.entity.EntityDataValue;

import java.util.List;

@AllArgsConstructor
@Getter
public class EntityDataEvent extends Event {
    private int entityId;
    private List<EntityDataValue<?>> data;
}
