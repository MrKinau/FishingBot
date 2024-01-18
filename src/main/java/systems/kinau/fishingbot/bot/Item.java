/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode(of = "eid")
public class Item {

    @Getter private int eid;
    @Getter @Setter private Integer itemId;
    @Getter @Setter private String name;
    @Getter @Setter private List<Enchantment> enchantments;
    @Getter @Setter private int motX;
    @Getter @Setter private int motY;
    @Getter @Setter private int motZ;
    @Getter private final double originX;
    @Getter private final double originY;
    @Getter private final double originZ;

    @Override
    public String toString() {
        return eid + ":" + name + " (" + motX + "/" + motY + "/" + motZ + ")";
    }

    public int getMaxMot() {
        return getMaxMot(motX, motY, motZ);
    }

    public static int getMaxMot(int motX, int motY, int motZ) {
        return Math.abs(motX) + Math.abs(motY) + Math.abs(motZ);

    }
}
