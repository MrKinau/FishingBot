/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@AllArgsConstructor
public class PacketOutPosLook extends Packet {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private boolean horizontalCollision;

    public PacketOutPosLook(float yaw, float pitch) {
        this(FishingBot.getInstance().getCurrentBot().getPlayer().getX(), FishingBot.getInstance().getCurrentBot().getPlayer().getY(), FishingBot.getInstance().getCurrentBot().getPlayer().getZ(), yaw, pitch, true, true);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeDouble(getX());
        out.writeDouble(getY());
        out.writeDouble(getZ());
        out.writeFloat(getYaw());
        out.writeFloat(getPitch());
        if (protocolId >= ProtocolConstants.MC_1_21_2_RC_2)
            out.writeByte(PacketOutPosition.packFlags(onGround, horizontalCollision));
        else
            out.writeBoolean(onGround);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
