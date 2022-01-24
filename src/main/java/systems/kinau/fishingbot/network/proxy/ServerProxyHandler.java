package systems.kinau.fishingbot.network.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import systems.kinau.fishingbot.Bot;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.gui.Dialogs;
import systems.kinau.fishingbot.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class ServerProxyHandler implements Runnable {

    private final Socket clientSocket;
    private Socket serverSocket;
    private final int protocolId;
    @Getter(AccessLevel.NONE)
    private final Consumer<ServerProxyHandler> onClientConnecting, onClientConnected, onClientDisconnected;

    @Setter
    private DataInputStream inFromClient, inFromServer;
    @Setter
    private DataOutputStream outToClient, outToServer;

    @Override
    public void run() {
        try {
            if (!acceptedConnection()) {
                FishingBot.getI18n().info("proxy-server-connection-rejected", clientSocket.getInetAddress().getHostAddress());
                clientSocket.close();
                return;
            }
            FishingBot.getI18n().info("proxy-server-connection-accepted", clientSocket.getInetAddress().getHostAddress());
            onClientConnecting.accept(this);
            if (FishingBot.getInstance().getCurrentBot() == null) {
                clientSocket.close();
                return;
            }
            Bot bot = FishingBot.getInstance().getCurrentBot();

            inFromClient = new DataInputStream(clientSocket.getInputStream());
            outToClient = new DataOutputStream(clientSocket.getOutputStream());

            try {
                this.serverSocket = new Socket(bot.getServerHost(), bot.getServerPort());
            } catch (IOException e) {
                e.printStackTrace();
                outToClient.flush();
            }

            inFromServer = new DataInputStream(serverSocket.getInputStream());
            outToServer = new DataOutputStream(serverSocket.getOutputStream());

            sendConnectPackets(outToServer);
            onClientConnected.accept(this);

            new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive() && bot.isRunning()) {
                        bot.getNet().readDataFromClient();
                    }
                } catch (Exception e) {
                    if (bot.isRunning())
                        e.printStackTrace();
                }
                try {
                    outToServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "proxyFormClientThread").start();

            try {
                while (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive() && bot.isRunning()) {
                    bot.getNet().readDataFromServer();
                }
            } catch (IOException e) {
                if (bot.isRunning())
                    e.printStackTrace();
            } finally {
                try {
                    if (serverSocket != null)
                        serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        onClientDisconnected.accept(this);
    }

    public void shutdown() {
        try {
            if (inFromClient != null)
                inFromClient.close();
            if (inFromServer != null)
                inFromServer.close();
            if (outToClient != null)
                outToClient.close();
            if (outToServer != null)
                outToServer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean acceptedConnection() {
        FishingBot.getI18n().info("proxy-server-new-connection", clientSocket.getInetAddress().getHostAddress());
        if (!FishingBot.getInstance().getConfig().isAcceptConnections()) return true;
        if (FishingBot.getInstance().getMainGUIController() != null)
            return Dialogs.showAcceptConnection(clientSocket.getInetAddress().getHostAddress());
        return Dialogs.showAcceptConnectionNoGui(clientSocket.getInetAddress().getHostAddress());
    }

    private void sendConnectPackets(DataOutputStream out) throws IOException {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        Packet.writeVarInt(0, buf);
        Packet.writeVarInt(protocolId, buf);
        Packet.writeString(FishingBot.getInstance().getCurrentBot().getServerHost(), buf);
        buf.writeShort(FishingBot.getInstance().getCurrentBot().getServerPort());
        Packet.writeVarInt(2, buf);
        Packet.send(buf, out);
    }
}
