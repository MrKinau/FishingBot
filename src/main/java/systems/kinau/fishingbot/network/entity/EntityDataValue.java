package systems.kinau.fishingbot.network.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityDataValue<T> {
    private final int elementIndex;
    private final int elementType;
    private final EntityDataElement<T> element;
}
