/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
public class PacketOutUseItem extends Packet {

    @Getter private NetworkHandler networkHandler;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                out.writeLong(-1);      //Position
                out.writeByte(255);     //Face
                Packet.writeSlot(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem(), out);  //Slot
                out.writeByte(0);       //Cursor X
                out.writeByte(0);       //Cursor Y
                out.writeByte(0);       //Cursor Z
                new Thread(() -> {
                    try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                    networkHandler.sendPacket(new PacketOutArmAnimation());
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
                out.writeByte(0);       //main hand
                break;
            }
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }
}
