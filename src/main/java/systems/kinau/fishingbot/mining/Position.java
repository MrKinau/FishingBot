/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/12
 */

package systems.kinau.fishingbot.mining;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Position {

    @Getter private int x;
    @Getter private int y;
    @Getter private int z;

}
