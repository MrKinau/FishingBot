package systems.kinau.fishingbot.network.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolState {
    HANDSHAKE("handshake"),
    LOGIN("login"),
    CONFIGURATION("configuration"),
    PLAY("play"),
    ;

    private final String id;
}
