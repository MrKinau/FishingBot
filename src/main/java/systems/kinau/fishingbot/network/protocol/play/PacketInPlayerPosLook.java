/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInPlayerPosLook extends Packet {

    @Getter private static double x, y, z;
    @Getter private static float yaw, pitch;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        if(in.readByte() == 0) {
            PacketInPlayerPosLook.x = x;
            PacketInPlayerPosLook.y = y;
            PacketInPlayerPosLook.z = z;
            PacketInPlayerPosLook.yaw = yaw;
            PacketInPlayerPosLook.pitch = pitch;
        }
        if(protocolId >= ProtocolConstants.MINECRAFT_1_14) {
            int tId = readVarInt(in);
        }
    }
}
