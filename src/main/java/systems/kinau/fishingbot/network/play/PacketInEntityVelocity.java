/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import java.io.IOException;

public class PacketInEntityVelocity extends Packet {

    private static short lastY = -1;

    @Override
    public void write(ByteArrayDataOutput out) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException {
        int eid = PacketHelper.readVarInt(in);
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
