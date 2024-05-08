/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.event.Event;

@Getter
@RequiredArgsConstructor
public class OpenWindowEvent extends Event {

    private final int windowId;
    private final int windowType;
    private final String title;

}
