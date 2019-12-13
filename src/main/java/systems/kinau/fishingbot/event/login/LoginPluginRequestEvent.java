package systems.kinau.fishingbot.event.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class LoginPluginRequestEvent extends Event {

    @Getter private int msgId;
    @Getter private String channel;
    @Getter private byte[] data;
}
