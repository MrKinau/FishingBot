package systems.kinau.fishingbot.ejection;

import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.network.utils.LocationUtils;

@Data
@AllArgsConstructor
public class ChestEjectFunction {

    private LocationUtils.Direction direction;
    private short slot;

}
