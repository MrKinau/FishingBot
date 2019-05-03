/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.ping;

import lombok.AllArgsConstructor;
import systems.kinau.fishingbot.FishingBot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@AllArgsConstructor
public class ServerPinger {

    private String serverName;
    private int serverPort;

    public void ping() {
        try {
            Socket socket = new Socket(serverName, serverPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //Somehow special calls
            //TODO: change this messed up Packetcalls
            PacketPingServer packet = new PacketPingServer(serverName, serverPort, out, in);
            packet.write(null);
            packet.read(null, null, 0);

            out.close();
            in.close();
            socket.close();

        } catch (UnknownHostException e) {
            FishingBot.getLog().severe("Unknown host: " + serverName);
        } catch (IOException e) {
            FishingBot.getLog().severe("Could not ping: " + serverName);
        }
    }
}
