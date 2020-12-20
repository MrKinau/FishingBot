package systems.kinau.fishingbot.fishing;

import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.network.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class EjectionRule {

    private String name = "default";
    private LocationUtils.Direction direction = LocationUtils.Direction.SOUTH;
    private List<String> allowList = new ArrayList<>();
    private EjectionType ejectionType = EjectionType.DROP;

    public enum EjectionType {
        DROP,
        FILL_CHEST
    }
}