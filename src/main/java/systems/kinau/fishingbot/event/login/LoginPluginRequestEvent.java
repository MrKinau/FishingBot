package systems.kinau.fishingbot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class LoginPluginRequestEvent extends Event {

    private int msgId;
    private String channel;
    private byte[] data;
}
