/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/10
 */

package systems.kinau.fishingbot.mining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.MaterialMc18;

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

    public MaterialMc18 getMaterial() {
        if(MineBot.getInstance().getServerProtocol() == ProtocolConstants.MINECRAFT_1_8)
            return MaterialMc18.getMaterial(id);
        else
            throw new IllegalStateException("Tried to get material of " + ProtocolConstants.getVersionString(MineBot.getInstance().getServerProtocol()) + "which is not implemented in " + MaterialMc18.class.getSimpleName());
    }
}
