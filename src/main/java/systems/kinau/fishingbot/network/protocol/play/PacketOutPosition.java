/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketOutPosition extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeDouble(PacketInPlayerPosLook.getX());
        out.writeDouble(PacketInPlayerPosLook.getY());
        out.writeDouble(PacketInPlayerPosLook.getZ());
        out.writeBoolean(true);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}
