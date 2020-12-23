/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.LocationUtils;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutUseItem extends Packet {

    @Getter private int x = -1;   // ||
    @Getter private int y = 4095; // \/
    @Getter private int z = -1;   // blockpos = -1
    @Getter private byte cursorX = 0;
    @Getter private byte cursorY = 0;
    @Getter private byte cursorZ = 0;
    @Getter private PacketOutBlockPlace.BlockFace blockFace = PacketOutBlockPlace.BlockFace.UNSET;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                out.writeLong(LocationUtils.toBlockPos(x, y, z));
                out.writeByte(blockFace == PacketOutBlockPlace.BlockFace.UNSET ? 255 : blockFace.ordinal());
                Packet.writeSlot(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem(), out);
                out.writeByte(cursorX);
                out.writeByte(cursorY);
                out.writeByte(cursorZ);
                new Thread(() -> {
                    try { Thread.sleep(100); } catch (InterruptedException ignore) { }
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutArmAnimation());
                }).start();
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
            default: {
                out.writeByte(PacketOutBlockPlace.Hand.MAIN_HAND.ordinal());       //main hand
                break;
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
