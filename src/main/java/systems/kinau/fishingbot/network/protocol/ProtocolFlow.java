package systems.kinau.fishingbot.network.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolFlow {
    INCOMING_PACKET("clientbound"),
    OUTGOING_PACKET("serverbound"),
    ;

    private final String id;
}
