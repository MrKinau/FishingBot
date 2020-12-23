package systems.kinau.fishingbot.modules.ejection;

import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.utils.LocationUtils;

@Data
@AllArgsConstructor
public class ChestEjectFunction {

    private LocationUtils.Direction direction;
    private short slot;

}
