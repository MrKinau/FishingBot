/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.UUID;

@AllArgsConstructor
public class ChatEvent extends Event {

    @Getter private String text;
    @Getter private UUID sender;
}
