/*
 * Created by David Luedtke (MrKinau)
 * 2020/2/8
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutClientStatus extends Packet {

    @Getter private int action;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        writeVarInt(action, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        //Only outgoing packet
    }
}
