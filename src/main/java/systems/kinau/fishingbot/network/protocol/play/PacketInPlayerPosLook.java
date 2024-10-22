/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.PosLookChangeEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
@Getter
public class PacketInPlayerPosLook extends Packet {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int teleportId;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_2_RC_2) {
            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();
            float yaw = in.readFloat();
            float pitch = in.readFloat();
            if (in.readByte() == 0) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.yaw = yaw;
                this.pitch = pitch;
                if (protocolId >= ProtocolConstants.MC_1_9) {
                    this.teleportId = readVarInt(in); //tID
                }
                if (protocolId >= ProtocolConstants.MC_1_17 && protocolId <= ProtocolConstants.MC_1_19_3) {
                    in.readBoolean(); // should dismount
                }
                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new PosLookChangeEvent(x, y, z, yaw, pitch, teleportId));
            }
        } else {
            this.teleportId = readVarInt(in);
            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();
            double dx = in.readDouble();
            double dy = in.readDouble();
            double dz = in.readDouble();
            float yaw = in.readFloat();
            float pitch = in.readFloat();
            int relatives = in.readInt();
            if (relatives == 0) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.yaw = yaw;
                this.pitch = pitch;
                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new PosLookChangeEvent(x, y, z, yaw, pitch, teleportId));
            }
        }
    }
}
