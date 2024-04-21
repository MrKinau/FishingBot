package systems.kinau.fishingbot.event.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.network.protocol.configuration.PacketInKnownResourcePacks;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class KnownResourcePacksRequestedEvent extends Event {
    private final List<PacketInKnownResourcePacks.KnownResourcePack> knownResourcePacks;
}
