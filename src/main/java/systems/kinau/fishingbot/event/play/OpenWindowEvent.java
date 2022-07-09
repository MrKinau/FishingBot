/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class OpenWindowEvent extends Event {

    @Getter
    private int windowId;
    @Getter
    private int windowType;
    @Getter
    private String title;
}
