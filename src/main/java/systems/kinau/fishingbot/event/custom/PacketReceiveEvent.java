package systems.kinau.fishingbot.event.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;

@AllArgsConstructor
@Getter
public class PacketReceiveEvent extends Event {
    private NetworkHandler.RawPacket rawPacket;
    private NetworkHandler.Participant sender, receiver;
    @Setter
    private boolean cancelled;

}
