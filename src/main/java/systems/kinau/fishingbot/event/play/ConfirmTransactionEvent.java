package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class ConfirmTransactionEvent extends Event {

    @Getter private byte windowId;
    @Getter private short action;
    @Getter private boolean accepted;
}
