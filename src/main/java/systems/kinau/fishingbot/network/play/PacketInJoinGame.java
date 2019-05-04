/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInJoinGame extends Packet {

    @Override
    public void write(ByteArrayDataOutput out) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) {
        in.readInt();   //Entity ID
        in.readByte();  //Gamemode
        in.readInt();   //Dimension
        in.readByte();  //MaxPlayer
        readString(in); //level type
        readVarInt(in); //viewDistance
        in.readByte(); //Reduced Debug info

        networkHandler.sendPacket(new PacketOutClientSettings());
    }
}
