package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.EntityMoveEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
@Getter
public class PacketInEntityPositionRotation extends Packet {

    private int entityId;
    private short dX;
    private short dY;
    private short dZ;
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
            this.dX = Integer.valueOf(in.readByte() * 128).shortValue();
            this.dY = Integer.valueOf(in.readByte() * 128).shortValue();
            this.dZ = Integer.valueOf(in.readByte() * 128).shortValue();
        } else {
            this.dX = in.readShort();
            this.dY = in.readShort();
            this.dZ = in.readShort();
        }
        this.yaw = in.readByte();
        this.pitch = in.readByte();
        this.onGround = in.readBoolean();
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityMoveEvent(entityId, dX, dY, dZ, yaw, pitch, onGround));
    }
}
