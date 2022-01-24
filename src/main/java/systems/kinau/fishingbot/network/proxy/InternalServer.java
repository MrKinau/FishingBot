package systems.kinau.fishingbot.network.proxy;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class InternalServer {

    private final String serverHost;
    private final int serverPort;
    private final ExecutorService service;
    private ServerSocket server;
    private Thread proxyThread;
    private Consumer<ServerProxyHandler> onClientConnecting, onClientConnected, onClientDisconnected;

    public InternalServer(int serverPort, String serverHost) {
        FishingBot.getI18n().info("proxy-server-starting");
        this.serverPort = serverPort;
        this.serverHost = serverHost;
        this.service = Executors.newCachedThreadPool();
    }

    public void start() {
        this.proxyThread = new Thread(() -> {
            try {
                this.server = new ServerSocket(serverPort, 0, InetAddress.getByName(serverHost));
                FishingBot.getI18n().info("proxy-server-started", serverHost, String.valueOf(serverPort));
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = server.accept();
                    try {
                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        Packet.readVarInt(in);
                        int id = Packet.readVarInt(in);
                        if (id != 0) {
                            socket.close();
                            continue;
                        }
                        int protocolId = Packet.readVarInt(in);
                        Packet.readString(in);
                        in.readShort();
                        int nextState = Packet.readVarInt(in);

                        switch (nextState) {
                            case 1:
                                service.submit(new ServerStatusHandler(socket, protocolId));
                                break;
                            case 2:
                                service.submit(new ServerProxyHandler(socket, protocolId, onClientConnecting, onClientConnected, onClientDisconnected));
                                break;
                            default:
                                socket.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (socket != null && !socket.isClosed())
                            socket.close();
                    }
                }
            } catch (Exception ex) {
                if (!(ex instanceof SocketException))
                    ex.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FishingBot.getInstance().setProxyServer(null);
            if (FishingBot.getInstance().getMainGUIController() != null) {
                FishingBot.getInstance().getMainGUIController().updateStartStop();
            }
        }, "proxyThread");
        proxyThread.start();
    }

    public void onClientConnecting(Consumer<ServerProxyHandler> handler) {
        this.onClientConnecting = handler;
    }

    public void onClientConnected(Consumer<ServerProxyHandler> handler) {
        this.onClientConnected = handler;
    }

    public void onClientDisconnected(Consumer<ServerProxyHandler> handler) {
        this.onClientDisconnected = handler;
    }

    public void shutdown() {
        FishingBot.getI18n().info("proxy-server-shutdown");
        if (FishingBot.getInstance().getCurrentBot() != null) {
            FishingBot.getInstance().getCurrentBot().setRunning(false);
            FishingBot.getInstance().getCurrentBot().getNet().getProxyConnection().shutdown();
        }
        if (service != null && !service.isShutdown())
            service.shutdownNow();
        if (proxyThread != null && proxyThread.isAlive())
            proxyThread.interrupt();
        try {
            if (server != null)
                server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FishingBot.getI18n().info("proxy-server-shutdown-finish");
    }
}
