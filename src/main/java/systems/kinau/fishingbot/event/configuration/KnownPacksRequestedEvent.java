package systems.kinau.fishingbot.event.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.network.protocol.configuration.PacketInKnownPacks;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class KnownPacksRequestedEvent extends Event {
    private final List<PacketInKnownPacks.KnownPack> knownPacks;
}
