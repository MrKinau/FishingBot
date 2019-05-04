/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInEntityVelocity extends Packet {

    private static short lastY = -1;

    @Override
    public void write(ByteArrayDataOutput out) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) {
        int eid = readVarInt(in);
        if(networkHandler.getFishingManager().getCurrentBobber() != eid)
            return;
        in.readShort();
        short y = in.readShort();
        in.readShort();
        if(Math.abs(y) > 350) {
            networkHandler.getFishingManager().fish();
        } else if(lastY == 0 && y == 0) {               //Sometimes Minecraft does not push the bobber down, but this workaround works good
            networkHandler.getFishingManager().fish();
        }
        lastY = y;
    }
}
