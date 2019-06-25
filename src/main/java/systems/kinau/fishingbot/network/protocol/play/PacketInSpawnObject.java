/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSpawnObject extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                int id = readVarInt(in);    //EID
                byte type = in.readByte();
                if(type == 90 && networkHandler.getFishingManager().isTrackingNextFishingId()) {   //90 = bobber
                    reFish(networkHandler, id);
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
                if(type == 90 && networkHandler.getFishingManager().isTrackingNextFishingId()) {   //90 = bobber
                    reFish(networkHandler, id);
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3: {
                int id = readVarInt(in);    //EID
                readUUID(in);               //E UUID
                int type = in.readByte();  //Obj type
                if(type == 101 && networkHandler.getFishingManager().isTrackingNextFishingId()) {   //101 = bobber
                    reFish(networkHandler, id);
                }
                break;
            }
        }
    }

    private void reFish(NetworkHandler networkHandler, int id) {
        networkHandler.getFishingManager().setTrackingNextFishingId(false);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException e) { }     //Prevent Velocity grabbed from flying hook
            networkHandler.getFishingManager().setCurrentBobber(id);
        }).start();
    }
}
