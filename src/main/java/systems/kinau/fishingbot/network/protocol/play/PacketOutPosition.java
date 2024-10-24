/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@AllArgsConstructor
public class PacketOutPosition extends Packet {

    private double x;
    private double y;
    private double z;
    private boolean onGround;
    private boolean horizontalCollision;

    static int packFlags(boolean onGround, boolean horizontalCollision) {
        int flags = 0;
        if (onGround) {
            flags |= 1;
        }

        if (horizontalCollision) {
            flags |= 2;
        }

        return flags;
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeDouble(getX());
        out.writeDouble(getY());
        out.writeDouble(getZ());
        if (protocolId >= ProtocolConstants.MC_1_21_2)
            out.writeByte(packFlags(onGround, horizontalCollision));
        else
            out.writeBoolean(onGround);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
