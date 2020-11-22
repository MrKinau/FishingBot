/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutLook extends Packet {

    @Getter private float yaw;
    @Getter private float pitch;
    @Getter private boolean onGround;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        out.writeFloat(getYaw());
        out.writeFloat(getPitch());
        out.writeBoolean(isOnGround());
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
