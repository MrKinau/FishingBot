package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@Getter
@AllArgsConstructor
public class PacketOutPlayerInput extends Packet {

    private Input input;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        input.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        // Only outgoing packet
    }

    @RequiredArgsConstructor
    public static class Input {
        private final boolean forward, backward, left, right, jump, sneak, sprint;

        public void write(ByteArrayDataOutput out, int protocolId) {
            byte packedByte = 0;
            packedByte = (byte) (packedByte | (forward ? 1 : 0));
            packedByte = (byte) (packedByte | (backward ? 2 : 0));
            packedByte = (byte) (packedByte | (left ? 4 : 0));
            packedByte = (byte) (packedByte | (right ? 8 : 0));
            packedByte = (byte) (packedByte | (jump ? 16 : 0));
            packedByte = (byte) (packedByte | (sneak ? 32 : 0));
            packedByte = (byte) (packedByte | (sprint ? 64 : 0));
            out.writeByte(packedByte);
        }
    }
}
