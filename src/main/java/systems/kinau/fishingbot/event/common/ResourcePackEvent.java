package systems.kinau.fishingbot.event.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import systems.kinau.fishingbot.event.Event;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class ResourcePackEvent extends Event {

    private UUID uuid;
    private String url;
    private String hash;
    private boolean forced;
    private String prompt;
}
