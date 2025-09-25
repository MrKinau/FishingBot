package systems.kinau.fishingbot.event.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.event.Event;

@Getter
@RequiredArgsConstructor
public class CodeOfConductEvent extends Event {
    private final String codeOfConduct;
}
