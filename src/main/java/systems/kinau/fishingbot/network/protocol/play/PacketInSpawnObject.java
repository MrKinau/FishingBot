/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.SpawnObjectEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSpawnObject extends Packet {

    @Getter private int id;
    @Getter private byte type;
    @Getter private int x;
    @Getter private int y;
    @Getter private int z;
    @Getter private byte yaw;
    @Getter private byte pitch;
    @Getter private int objectData;
    @Getter private short xVelocity;
    @Getter private short yVelocity;
    @Getter private short zVelocity;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                this.id = readVarInt(in);       // EID
                this.type = in.readByte();      // Type of the object
                this.x = in.readInt();          // X POS
                this.y = in.readInt();          // Y POS
                this.z = in.readInt();          // Z POS
                this.pitch = in.readByte();     // Pitch
                this.yaw = in.readByte();       // Yaw
                this.objectData = in.readInt(); // Data of object: https://wiki.vg/Object_Data
                if(getObjectData() != 0) {
                    this.xVelocity = in.readShort();    // Velocity only present if data nonzero
                    this.yVelocity = in.readShort();
                    this.zVelocity = in.readShort();
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            case ProtocolConstants.MINECRAFT_1_15:
            case ProtocolConstants.MINECRAFT_1_15_1:
            case ProtocolConstants.MINECRAFT_1_15_2:
            default: {
                this.id = readVarInt(in);       // EID
                readUUID(in);                   // E UUID
                this.type = in.readByte();      // Obj type
                this.x = (int)in.readDouble();  // X POS (casts are incorrect, but if nobody sees it, nobody can blame it)
                this.y = (int)in.readDouble();  // Y POS
                this.z = (int)in.readDouble();  // Z POS
                this.pitch = in.readByte();     // Pitch
                this.yaw = in.readByte();       // Yaw
                this.objectData = in.readInt(); // Data of object: https://wiki.vg/Object_Data
                if(getObjectData() != 0) {
                    this.xVelocity = in.readShort();    // Velocity only present if data nonzero
                    this.yVelocity = in.readShort();
                    this.zVelocity = in.readShort();
                }
                break;
            }
        }

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(
                new SpawnObjectEvent(getId(), getType(), getX(), getY(), getZ(), getYaw(), getPitch(), getObjectData(), getXVelocity(), getYVelocity(), getZVelocity()));
    }
}
