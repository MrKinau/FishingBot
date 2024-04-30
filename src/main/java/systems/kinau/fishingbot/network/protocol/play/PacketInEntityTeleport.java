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
        if (protocolId <= ProtocolConstants.MINECRAFT_1_8) {
            this.x = in.readInt() / 32.0;
            this.y = in.readInt() / 32.0;
            this.z = in.readInt() / 32.0;
        } else {
            this.x = in.readDouble();
            this.y = in.readDouble();
            this.z = in.readDouble();
        }
        this.yaw = in.readByte();
        this.pitch = in.readByte();
        this.onGround = in.readBoolean();
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityTeleportEvent(entityId, x, y, z, yaw, pitch, onGround));
    }
}
