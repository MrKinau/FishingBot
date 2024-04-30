package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@Getter
@AllArgsConstructor
public class ConfirmTransactionEvent extends Event {

    private byte windowId;
    private short action;
    private boolean accepted;
}
