package systems.kinau.fishingbot.event.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
@ToString
public class ResourcePackEvent extends Event {

    @Getter private String url;
    @Getter private String hash;
    @Getter private boolean forced;
    @Getter private String prompt;
}
