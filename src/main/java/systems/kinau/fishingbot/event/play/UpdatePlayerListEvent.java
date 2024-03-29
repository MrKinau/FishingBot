/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public class UpdatePlayerListEvent extends Event {

    @Getter private Action action;
    @Getter private Set<UUID> players;

    public static enum Action {
        REPLACE,
        ADD,
        REMOVE
    }
}
