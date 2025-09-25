/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.EntityVelocityEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
@Getter
public class PacketInEntityVelocity extends Packet {

    private short x;
    private short y;
    private short z;
    private int eid;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        this.eid = readVarInt(in);
        if (protocolId <= ProtocolConstants.MC_1_21_7) {
            this.x = in.readShort();
            this.y = in.readShort();
            this.z = in.readShort();
        } else {
            double[] velocity = readLpVec3(in);
            this.x = (short) (velocity[0] * 8000.0);
            this.y = (short) (velocity[1] * 8000.0);
            this.z = (short) (velocity[2] * 8000.0);
        }

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new EntityVelocityEvent(x, y, z, eid));
    }
}
