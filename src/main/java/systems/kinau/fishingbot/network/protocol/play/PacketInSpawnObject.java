/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSpawnObject extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if(!(MineBot.getInstance().getManager() instanceof FishingManager))
            return;
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                int id = readVarInt(in);    //EID
                byte type = in.readByte();
                if(type == 90 && ((FishingManager)MineBot.getInstance().getManager()).isTrackingNextFishingId()) {   //90 = bobber
                    reFish(id);
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
            case ProtocolConstants.MINECRAFT_1_9: {
                int id = readVarInt(in);    //EID
                readUUID(in);               //E UUID
                int type = in.readByte();  //Obj type
                if(type == 90 && ((FishingManager)MineBot.getInstance().getManager()).isTrackingNextFishingId()) {   //90 = bobber
                    reFish(id);
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                int id = readVarInt(in);    //EID
                readUUID(in);               //E UUID
                int type = in.readByte();  //Obj type
                if(type == 101 && ((FishingManager)MineBot.getInstance().getManager()).isTrackingNextFishingId()) {   //101 = bobber
                    reFish(id);
                }
                break;
            }
        }
    }

    private void reFish(int id) {
        ((FishingManager)MineBot.getInstance().getManager()).setTrackingNextFishingId(false);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException e) { }     //Prevent Velocity grabbed from flying hook
            ((FishingManager)MineBot.getInstance().getManager()).setCurrentBobber(id);
        }).start();
    }
}
