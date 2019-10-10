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
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.TextComponent;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

@AllArgsConstructor
public class ServerPinger {

    private String serverName;
    private int serverPort;
    private MineBot mineBot;

    public void ping() {
        if(serverName == null || serverName.trim().isEmpty()) {
            MineBot.getLog().severe("Invalid server host given. Please change the server-ip in your config.properties");
            System.exit(1);
        }

        //Getting SRV Record - changing data to correct ones
        if(serverPort == 25565 || serverPort < 1) {
            String[] serverData = getServerAddress(serverName);
            if(!serverData[0].equalsIgnoreCase(serverName))
                MineBot.getLog().info("Changed server host to: " + serverData[0]);
            this.serverName = serverData[0];
            this.serverPort = Integer.valueOf(serverData[1]);
            if(serverPort != 25565)
                MineBot.getLog().info("Changed port to: " + serverPort);
        }

        MineBot.setServerHost(serverName);
        MineBot.setServerPort(serverPort);

        try {

            Socket socket = new Socket(serverName, serverPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //send Handshake 0x00 - PING

            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            Packet.writeVarInt(ProtocolConstants.getProtocolId(MineBot.getConfig().getDefaultProtocol()), buf);
            Packet.writeString(serverName, buf);
            buf.writeShort(serverPort);
            Packet.writeVarInt(1, buf);

            send(buf, out);

            buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            send(buf, out);

            //read Handshake 0x00 Response - Ping

            Packet.readVarInt(in); //ignore
            int id = Packet.readVarInt(in);

            if (id == 0) {
                String pong = Packet.readString(in);
                JsonObject root = new JsonParser().parse(pong).getAsJsonObject();
                MineBot.getLog().info(root.toString());
                int protocolId = root.getAsJsonObject("version").get("protocol").getAsInt();
                if(!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(protocolId)) {
                    MineBot.getLog().severe("This server is not running a supported protocol version!");
                    MineBot.getLog().severe("It is possibe that it wont work correctly");

                    //Register protocol of 1.14 for unknown versions
                    mineBot.getNet().getPlayRegistry_IN().get(protocolId).copyOf(mineBot.getNet().getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14));
                    mineBot.getNet().getPlayRegistry_OUT().get(protocolId).copyOf(mineBot.getNet().getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14));
                }
                MineBot.setServerProtocol(protocolId);
                String description = "Unknown";
                try {
                    try {
                        if (protocolId > ProtocolConstants.MINECRAFT_1_8)
                            description = root.getAsJsonObject("description").get("text").getAsString();
                        else
                            description = root.get("description").getAsString();
                    } catch (UnsupportedOperationException ex) {
                        description = TextComponent.toPlainText(root.getAsJsonObject("description"));
                    }
                } catch (UnsupportedOperationException ex) {
                } finally {
                    if(description.trim().isEmpty())
                        description = "Unknown";
                }
                MineBot.getLog().info("Received pong: " + description + ", Version: " + ProtocolConstants.getVersionString(protocolId));
            }

            out.close();
            in.close();
            socket.close();

        } catch (UnknownHostException e) {
            MineBot.getLog().severe("Unknown host: " + serverName);
        } catch (IOException e) {
            MineBot.getLog().severe("Could not ping: " + serverName);
        }
    }

    /**
     * Returns a server's address and port for the specified hostname, looking up the SRV record if possible
     * Copied from Minecraft src
     */
    private static String[] getServerAddress(String serverHost) {
        try {
            String s = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            DirContext dircontext = new InitialDirContext(hashtable);
            Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + serverHost, new String[] {"SRV"});
            String[] astring = attributes.get("srv").get().toString().split(" ", 4);
            return new String[] {astring[3], astring[2]};
        } catch (Throwable var6) {
            return new String[] {serverHost, Integer.toString(25565)};
        }
    }

    private void send(ByteArrayDataOutput buf, DataOutputStream out) throws IOException {
        ByteArrayDataOutput sender = ByteStreams.newDataOutput();
        Packet.writeVarInt(buf.toByteArray().length, sender);
        sender.write(buf.toByteArray());
        out.write(sender.toByteArray());
        out.flush();
    }
}
