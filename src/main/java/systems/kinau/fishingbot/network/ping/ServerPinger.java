/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.ping;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.utils.TextComponent;

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

    public void ping() {
        FishingBot.getInstance().getCurrentBot().setServerProtocol(ProtocolConstants.getProtocolId(FishingBot.getInstance().getCurrentBot().getConfig().getDefaultProtocol()));
        if (serverName == null || serverName.trim().isEmpty()) {
            FishingBot.getI18n().severe("network-invalid-server-address");
            FishingBot.getInstance().getCurrentBot().setRunning(false);
            FishingBot.getInstance().getCurrentBot().setWontConnect(true);
            return;
        }

        updateWithSRV();

        try {

            Socket socket = new Socket(serverName, serverPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //send Handshake 0x00 - PING

            int pingProtocol = (ProtocolConstants.getProtocolId(FishingBot.getInstance().getCurrentBot().getConfig().getDefaultProtocol()));
            if (pingProtocol == ProtocolConstants.AUTOMATIC)
                pingProtocol = ProtocolConstants.getLatest();

            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            Packet.writeVarInt(pingProtocol, buf);
            Packet.writeString(serverName, buf);
            buf.writeShort(serverPort);
            Packet.writeVarInt(1, buf);

            send(buf, out);

            buf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, buf);
            send(buf, out);

            //read Handshake 0x00 Response - Ping

            //TODO: Sometimes it's an int sometimes a varint? investigate how to fix the auto-version detection (e.g. based on getAvailable())
            Packet.readVarInt(in); //ignore
//            in.readInt(); //ignore
            Packet.readVarInt(in); //id

//            if (id != 2) {
            String pong = Packet.readString(in);
            JSONObject root = (JSONObject) new JSONParser().parse(pong);
            long protocolId = (long) ((JSONObject)root.get("version")).get("protocol");
            long currPlayers = (long) ((JSONObject)root.get("players")).get("online");

            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.AUTOMATIC)
                FishingBot.getInstance().getCurrentBot().setServerProtocol(Long.valueOf(protocolId).intValue());
            else if (protocolId != FishingBot.getInstance().getCurrentBot().getServerProtocol()) {
                FishingBot.getI18n().warning("network-ping-differs-protocol",
                        "\"" + ProtocolConstants.getVersionString(Long.valueOf(protocolId).intValue()) + "\" (" + protocolId + ")",
                        "\"" + ProtocolConstants.getVersionString(FishingBot.getInstance().getCurrentBot().getServerProtocol()) + "\" (" + FishingBot.getInstance().getCurrentBot().getServerProtocol() + ")");
            }
            String description = "Unknown";
            try {
                try {
                    if (protocolId > ProtocolConstants.MINECRAFT_1_8)
                        description = (String) ((JSONObject)root.get("description")).get("text");
                    else
                        description = (String) root.get("description");
                } catch (UnsupportedOperationException ex) {
                    description = TextComponent.toPlainText(((JSONObject)root.get("description")));
                }
            } catch (UnsupportedOperationException ignored) {
            } finally {
                if (description.trim().isEmpty())
                    description = "Unknown";
            }

            FishingBot.getI18n().info("network-received-pong", description, ProtocolConstants.getVersionString(Long.valueOf(protocolId).intValue()), String.valueOf(protocolId), String.valueOf(currPlayers));
            if (currPlayers >= FishingBot.getInstance().getCurrentBot().getConfig().getAutoDisconnectPlayersThreshold() && FishingBot.getInstance().getCurrentBot().getConfig().isAutoDisconnect()) {
                FishingBot.getI18n().warning("network-server-is-full");
                FishingBot.getInstance().getCurrentBot().setWontConnect(true);
            }
//            }

            out.close();
            in.close();
            socket.close();

        } catch (UnknownHostException e) {
            FishingBot.getI18n().severe("network-unknown-host", serverName);
        } catch (Exception e) {
            FishingBot.getI18n().severe("network-could-not-ping", serverName);
            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.AUTOMATIC)
                FishingBot.getInstance().getCurrentBot().setServerProtocol(ProtocolConstants.getLatest());
            e.printStackTrace();
        }
    }

    public void updateWithSRV() {
        //Getting SRV Record - changing data to correct ones
        if (serverPort == 25565 || serverPort < 1) {
            String[] serverData = getServerAddress(serverName);
            if (!serverData[0].equalsIgnoreCase(serverName))
                FishingBot.getI18n().info("network-changed-address", serverData[0]);
            this.serverName = serverData[0];
            this.serverPort = Integer.valueOf(serverData[1]);
            if (serverPort != 25565)
                FishingBot.getI18n().info("network-changed-port", String.valueOf(serverPort));
        }

        FishingBot.getInstance().getCurrentBot().setServerHost(serverName);
        FishingBot.getInstance().getCurrentBot().setServerPort(serverPort);
    }

    /**
     * Returns a server's address and port for the specified hostname, looking up the SRV record if possible
     * Copied from Minecraft src
     */
    private static String[] getServerAddress(String serverHost) {
        try {
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            DirContext dircontext = new InitialDirContext(hashtable);
            Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + serverHost, new String[]{"SRV"});
            String[] astring = attributes.get("srv").get().toString().split(" ", 4);
            return new String[]{astring[3], astring[2]};
        } catch (Throwable var6) {
            return new String[]{serverHost, Integer.toString(25565)};
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
