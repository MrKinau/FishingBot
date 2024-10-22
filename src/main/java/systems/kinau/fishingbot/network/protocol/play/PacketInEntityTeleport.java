package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.EntityTeleportEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
@Getter
public class PacketInEntityTeleport extends Packet {

    private int entityId;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.entityId = readVarInt(in);
        if (protocolId <= ProtocolConstants.MC_1_8) {
            this.x = in.readInt() / 32.0;
            this.y = in.readInt() / 32.0;
            this.z = in.readInt() / 32.0;
        } else {
            this.x = in.readDouble();
            this.y = in.readDouble();
            this.z = in.readDouble();
            if (protocolId >= ProtocolConstants.MC_1_21_2_RC_2) {
                double dx = in.readDouble();
                double dy = in.readDouble();
                double dz = in.readDouble();
            }
        }
        int relatives = 0;
        if (protocolId <= ProtocolConstants.MC_1_21) {
            this.yaw = in.readByte();
            this.pitch = in.readByte();
        } else {
            this.yaw = Double.valueOf(Math.floor(in.readFloat() * 256.0F / 360.0F)).byteValue();
            this.pitch = Double.valueOf(Math.floor(in.readFloat() * 256.0F / 360.0F)).byteValue();
            relatives = in.readInt();
        }
        this.onGround = in.readBoolean();
        if (relatives == 0)
            FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityTeleportEvent(entityId, x, y, z, yaw, pitch, onGround));
    }
}
