/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginSuccessEvent extends Event {

    private UUID uuid;
    private String userName;
}
