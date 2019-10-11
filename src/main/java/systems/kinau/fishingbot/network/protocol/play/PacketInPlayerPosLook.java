/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.mining.Player;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInPlayerPosLook extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if(MineBot.getInstance().getPlayer() == null)
            MineBot.getInstance().setPlayer(new Player());
        Player player = MineBot.getInstance().getPlayer();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        if(in.readByte() == 0) {
            player.setX(x);
            player.setY(y);
            player.setZ(z);
            player.setYaw(yaw);
            player.setPitch(pitch);
            player.onGroundCheck();
        }
        if(protocolId >= ProtocolConstants.MINECRAFT_1_14) {
            readVarInt(in); //tID
        }
    }
}
