package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.network.protocol.play.PacketOutBlockPlace;

@Data
@AllArgsConstructor
public class MovingObjectPositionBlock {
    private long blockPos;
    private PacketOutBlockPlace.BlockFace direction;
    private float dx;
    private float dy;
    private float dz;
    private boolean flag;
}
