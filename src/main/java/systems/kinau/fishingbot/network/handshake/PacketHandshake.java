/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.handshake;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import systems.kinau.fishingbot.io.Constants;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketHandshake extends Packet {

    private String serverName;
    private int serverPort;

    @Override
    public void write(ByteArrayDataOutput out) throws IOException {
        writeVarInt(Constants.PROTOCOL_ID, out);
        writeString(serverName, out);
        out.writeShort(serverPort);
        writeVarInt(2, out); //next State = 2 -> LOGIN
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException { }
}
