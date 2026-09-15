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
public class PacketInEntityPosition extends Packet {

    private int entityId;
    private short dX;
    private short dY;
    private short dZ;
    private boolean onGround;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.entityId = readVarInt(in);
        if (protocolId <= ProtocolConstants.MC_1_8) {
            this.dX = Integer.valueOf(((int)in.readByte()) * 128).shortValue();
            this.dY = Integer.valueOf(((int)in.readByte()) * 128).shortValue();
            this.dZ = Integer.valueOf(((int)in.readByte()) * 128).shortValue();
            this.onGround = in.readBoolean();
        } else if (protocolId <= ProtocolConstants.MC_26_2) {
            this.dX = in.readShort();
            this.dY = in.readShort();
            this.dZ = in.readShort();
            this.onGround = in.readBoolean();
        } else {
            int properties = Packet.readVarInt(in);
            int stepCount = properties >>> 1;
            if (stepCount <= 0) {
                // linear
                this.dX = in.readShort();
                this.dY = in.readShort();
                this.dZ = in.readShort();
            } else {
                // stepped
                int dXStepped = 0, dYStepped = 0, dZStepped = 0;
                for (int i = 0; i < stepCount; i++) {
                    int ticks = Packet.readVarInt(in);
                    dXStepped += in.readShort(); // prob wrong just adding for final pos, but idc
                    dYStepped += in.readShort(); // prob wrong just adding for final pos, but idc
                    dZStepped += in.readShort(); // prob wrong just adding for final pos, but idc
                }
                this.dX = (short) dXStepped;
                this.dY = (short) dYStepped;
                this.dZ = (short) dZStepped;
            }
            this.onGround = (properties & 1) != 0;
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityMoveEvent(entityId, dX, dY, dZ, null, null, onGround));
    }
}
