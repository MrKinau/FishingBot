/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/10
 */

package systems.kinau.fishingbot.mining;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BlockType {

    public static final BlockType AIR = new BlockType(0, 0);

    public BlockType(int block) {
        id = (block & 0xfff0) >> 4;
        data = block & 0xF;
    }

    @Getter private int id;
    @Getter private int data;

    public short getBlock() {
        return (short) (id << 4 | (data & 15));
    }
}
