/*
 * Created by David Luedtke (MrKinau)
 * 2020/2/8
 */

package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.utils.MaterialMc18;

@AllArgsConstructor
public class ItemStack {

    @Getter private short itemId;
    @Getter private byte count;
    @Getter private short damage;
    @Getter private byte[] nbtData;

    public MaterialMc18 getMaterial() {
        return MaterialMc18.getMaterial(itemId);
    }
}
