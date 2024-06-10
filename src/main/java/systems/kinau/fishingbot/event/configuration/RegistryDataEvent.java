package systems.kinau.fishingbot.event.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

import java.util.SortedMap;

@Getter
@RequiredArgsConstructor
public class RegistryDataEvent extends Event {
    private final String registryId;
    private final SortedMap<String, @Nullable NBTTag> registryData;
}
