package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.EntityTeleportEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
@Getter
public class PacketInEntityPositionSync extends Packet {

    private int entityId;
    private double x;
    private double y;
    private double z;
    private double dx;
    private double dy;
    private double dz;
    private float yaw;
    private float pitch;
    private boolean onGround;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.entityId = readVarInt(in);
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.dx = in.readDouble();
        this.dy = in.readDouble();
        this.dz = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.onGround = in.readBoolean();
        byte packedYaw = Double.valueOf(Math.floor(yaw * 256.0F / 360.0F)).byteValue();
        byte packedPitch = Double.valueOf(Math.floor(pitch * 256.0F / 360.0F)).byteValue();
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityTeleportEvent(entityId, x, y, z, packedYaw, packedPitch, onGround));
    }
}
