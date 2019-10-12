/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/12
 */

package systems.kinau.fishingbot.event.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;
import systems.kinau.fishingbot.mining.BlockType;

@AllArgsConstructor
public class BlockChangeEvent extends Event {

    @Getter private int x;
    @Getter private int y;
    @Getter private int z;
    @Getter private BlockType block;

}
