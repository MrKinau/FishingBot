/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChatEvent extends Event {

    private String text;
    private UUID sender;
}
