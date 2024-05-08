/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.SpawnMobEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PacketInSpawnMob extends Packet {

    private int eID;
    private UUID uuid;
    private int type;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private byte headPitch;
    private short velocityX;
    private short velocityY;
    private short velocityZ;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        // Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.eID = readVarInt(in);
        this.uuid = readUUID(in);
        this.type = readVarInt(in);
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.yaw = in.readByte();
        this.pitch = in.readByte();
        this.headPitch = in.readByte();
        this.velocityX = in.readShort();
        this.velocityY = in.readShort();
        this.velocityZ = in.readShort();

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new SpawnMobEvent(eID, uuid, type, x, y, z, yaw, pitch, headPitch, velocityX, velocityY, velocityZ));
    }
}
