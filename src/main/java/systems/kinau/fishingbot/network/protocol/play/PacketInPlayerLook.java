package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.LookChangeEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.LocationUtils;

@NoArgsConstructor
@Getter
public class PacketInPlayerLook extends Packet {

    private float yaw;
    private float pitch;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId <= ProtocolConstants.MC_1_21_7) {
            this.yaw = in.readFloat();
            this.pitch = in.readFloat();
        } else {
            this.yaw = in.readFloat();
            boolean relativeYaw = in.readBoolean();
            this.pitch = in.readFloat();
            boolean relativePitch = in.readBoolean();
            if (relativeYaw) {
                float prevYaw = FishingBot.getInstance().getCurrentBot().getPlayer().getYaw();
                this.yaw = LocationUtils.normalizeYaw(prevYaw + this.yaw);
            }
            if (relativePitch) {
                float prevPitch = FishingBot.getInstance().getCurrentBot().getPlayer().getPitch();
                this.pitch = LocationUtils.normalizePitch(prevPitch + this.pitch);
            }
        }
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new LookChangeEvent(yaw, pitch));
    }
}
