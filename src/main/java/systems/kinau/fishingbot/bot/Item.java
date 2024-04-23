/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "eid")
public class Item {

    private int eid;
    @Setter private Integer itemId;
    @Setter private String name;
    @Setter private List<Enchantment> enchantments;
    @Setter private int motX;
    @Setter private int motY;
    @Setter private int motZ;
    private final double originX;
    private final double originY;
    private final double originZ;

    @Override
    public String toString() {
        return eid + ":" + name + " (" + motX + "/" + motY + "/" + motZ + ")";
    }

    public int getMaxMot() {
        return getMaxMot(motX, motY, motZ);
    }

    public String getDisplayName() {
        return FishingBot.getInstance().getCurrentBot().getMinecraftTranslations().getItemName(getName());
    }

    public static int getMaxMot(int motX, int motY, int motZ) {
        return Math.abs(motX) + Math.abs(motY) + Math.abs(motZ);
    }
}
