package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.List;

@Getter
@AllArgsConstructor
public class DestroyEntitiesEvent extends Event {

    private List<Integer> entityIds;

}
