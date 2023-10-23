package systems.kinau.fishingbot.modules.ejection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import systems.kinau.fishingbot.gui.config.DisplayNameProvider;
import systems.kinau.fishingbot.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class EjectionRule {

    private String name = "default";
    private LocationUtils.Direction direction = LocationUtils.Direction.SOUTH;
    private List<String> allowList = new ArrayList<>();
    private EjectionType ejectionType = EjectionType.DROP;

    @AllArgsConstructor
    @Getter
    public enum EjectionType implements DisplayNameProvider {
        DROP("Drop Items"),
        FILL_CHEST("Fill Adjacent Chest");

        private final String displayName;
    }
}