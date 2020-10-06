package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.List;

@AllArgsConstructor
public class DestroyEntitiesEvent extends Event {

    @Getter private List<Integer> entityIds;

}
