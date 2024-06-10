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
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.LocationUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PacketOutUseItem extends Packet {

    private int x = -1;   // ||
    private int y = 4095; // \/
    private int z = -1;   // blockpos = -1
    private byte cursorX = 0;
    private byte cursorY = 0;
    private byte cursorZ = 0;
    private float yaw = 0;
    private float pitch = 0;
    private PacketOutBlockPlace.BlockFace blockFace = PacketOutBlockPlace.BlockFace.UNSET;

    public PacketOutUseItem(Player player) {
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId == ProtocolConstants.MINECRAFT_1_8) {
            out.writeLong(LocationUtils.toBlockPos(x, y, z));
            out.writeByte(blockFace == PacketOutBlockPlace.BlockFace.UNSET ? 255 : blockFace.ordinal());
            Packet.writeSlot(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem(), out, protocolId);
            out.writeByte(cursorX);
            out.writeByte(cursorY);
            out.writeByte(cursorZ);
            new Thread(() -> {
                try { Thread.sleep(100); } catch (InterruptedException ignore) { }
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutArmAnimation());
            }).start();
        } else {
            writeVarInt(PacketOutBlockPlace.Hand.MAIN_HAND.ordinal(), out);
            if (protocolId >= ProtocolConstants.MINECRAFT_1_19)
                writeVarInt(0, out); //sequence
            if (protocolId >= ProtocolConstants.MINECRAFT_1_21_RC_1) {
                out.writeFloat(yaw);
                out.writeFloat(pitch);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
