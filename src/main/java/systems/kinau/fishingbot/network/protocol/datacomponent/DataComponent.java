package systems.kinau.fishingbot.network.protocol.datacomponent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class DataComponent implements DataComponentPart{

    private final int componentTypeId;

}
