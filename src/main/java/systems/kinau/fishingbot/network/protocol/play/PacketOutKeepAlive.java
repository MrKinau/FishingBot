/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutKeepAlive extends Packet {

    @Getter private long id;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if(protocolId <= ProtocolConstants.MINECRAFT_1_12_1) {
            writeVarInt(Long.valueOf(getId()).intValue(), out);
        } else {
            out.writeLong(id);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }
}
