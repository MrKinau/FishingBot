package systems.kinau.fishingbot.event.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class PluginMessageEvent extends Event {

    private String channel;
    private byte[] data;
}
