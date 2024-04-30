package systems.kinau.fishingbot.network.item.datacomponent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class DataComponent implements DataComponentPart{

    private final int componentTypeId;

}
