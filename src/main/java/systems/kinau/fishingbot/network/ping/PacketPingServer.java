/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */


package systems.kinau.fishingbot.network.ping;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@AllArgsConstructor
public class PacketPingServer extends Packet {

    private String serverName;
    private int serverPort;
    private DataOutputStream receiver;
    private DataInputStream sender;

    @Override
    public void write(ByteArrayDataOutput out) throws IOException{
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        writeVarInt(0, buf);
        writeVarInt(4, buf);
        writeString(serverName, buf);
        buf.writeShort(serverPort);
        writeVarInt(1, buf);

        send(buf, receiver);

        buf = ByteStreams.newDataOutput();
        writeVarInt(0, buf);
        send(buf, receiver);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException {
        readVarInt(sender); //ignore
        int id = readVarInt(sender);

        if (id == 0) {
            String pong = readString(sender);
            JsonObject root = new JsonParser().parse(pong).getAsJsonObject();
            if(root.getAsJsonObject("version").get("protocol").getAsInt() != 477)
                FishingBot.getLog().warning("This server is not running a supported protocol version! You might not connect!");
            FishingBot.getLog().info("Received pong: " + root.getAsJsonObject("description").get("text").getAsString());
        }
    }

    private void send(ByteArrayDataOutput buf, DataOutputStream out) throws IOException {
        ByteArrayDataOutput sender = ByteStreams.newDataOutput();
        writeVarInt(buf.toByteArray().length, sender);
        sender.write(buf.toByteArray());
        out.write(sender.toByteArray());
        out.flush();
    }
}
