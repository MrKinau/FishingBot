package systems.kinau.fishingbot.network.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class ServerStatusHandler implements Runnable {

    private Socket socket;
    private int protocolId;

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            Packet.writeString(getServerStatus(protocolId), buf);
            Packet.send(buf, out);

            Packet.readVarInt(in);
            long pingPayload = in.readLong();

            buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(1, buf);
            buf.writeLong(pingPayload);
            Packet.send(buf, out);
            socket.close();
        } catch (Exception ignore) {}
    }

    private String getServerStatus(int protocolId) {
        return  "{" +
                "\"version\": {" +
                "\"name\": \"" + ProtocolConstants.getVersionString(protocolId) + "\"," +
                "\"protocol\": " + protocolId +
                "}," +
                "\"players\": {" +
                "\"max\": 1337," +
                "\"online\": 0," +
                "\"sample\": []" +
                "}," +
                "\"description\": {" +
                "\"text\": \"" + FishingBot.NAME_AND_VERSION +  "\"" +
                "}" +
                "}";
    }
}
