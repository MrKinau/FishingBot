/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.network.handshake.PacketHandshake;
import systems.kinau.fishingbot.network.login.*;
import systems.kinau.fishingbot.network.play.*;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class NetworkHandler {

    @Getter private AuthData authData;
    @Getter private  FishingManager fishingManager;

    @Getter private Socket socket;
    @Getter private DataOutputStream out;
    @Getter private DataInputStream in;

    @Getter @Setter private State state;
    @Getter private PacketRegistry handshakeRegistry, loginRegistry_IN, loginRegistry_OUT, playRegistry_IN, playRegistry_OUT;

    @Getter @Setter private int threshold = 0;
    @Getter @Setter PublicKey publicKey;
    @Getter @Setter SecretKey secretKey;
    @Getter @Setter private boolean outputEncrypted;
    @Getter @Setter private boolean inputBeingDecrypted;

    public NetworkHandler(Socket socket, AuthData authData, FishingManager fishingManager) {
        this.socket = socket;
        this.authData = authData;
        this.fishingManager = fishingManager;
        fishingManager.setNetworkHandler(this);
        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());

            this.state = State.HANDSHAKE;
            initPacketRegistries();
        } catch (IOException e) {
            FishingBot.getLog().severe("Could not start bot: IOException");
        }
    }

    private void initPacketRegistries() {
        this.handshakeRegistry = new PacketRegistry();
        this.loginRegistry_IN = new PacketRegistry();
        this.loginRegistry_OUT = new PacketRegistry();
        this.playRegistry_IN = new PacketRegistry();
        this.playRegistry_OUT = new PacketRegistry();

        getHandshakeRegistry().registerPacket(0x00, PacketHandshake.class);

        getLoginRegistry_IN().registerPacket(0x00, PacketInLoginDisconnect.class);
        getLoginRegistry_IN().registerPacket(0x01, PacketInEncryptionRequest.class);
        getLoginRegistry_IN().registerPacket(0x02, PacketInLoginSuccess.class);
        getLoginRegistry_IN().registerPacket(0x03, PacketInSetCompression.class);

        getLoginRegistry_OUT().registerPacket(0x00, PacketOutLoginStart.class);
        getLoginRegistry_OUT().registerPacket(0x01, PacketOutEncryptionResponse.class);

        getPlayRegistry_IN().registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistry_IN().registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistry_IN().registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistry_IN().registerPacket(0x45, PacketInEntityVelocity.class);
        getPlayRegistry_IN().registerPacket(0x43, PacketInEntityMetadata.class);

        getPlayRegistry_OUT().registerPacket(0x03, PacketOutChat.class);
        getPlayRegistry_OUT().registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistry_OUT().registerPacket(0x0F, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().registerPacket(0x2D, PacketOutUseItem.class);
    }

    public void sendPacket(Packet packet) {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();

        //Add Packet ID from current PacketRegistry
        switch (getState()) {
            case HANDSHAKE:
                PacketHelper.writeVarInt(buf, getHandshakeRegistry().getId(packet.getClass()));
                break;
            case LOGIN:
                PacketHelper.writeVarInt(buf, getLoginRegistry_OUT().getId(packet.getClass()));
                break;
            case PLAY:
                PacketHelper.writeVarInt(buf, getPlayRegistry_OUT().getId(packet.getClass()));
                break;
        }

        //Add packet payload
        try {
            packet.write(buf);
        } catch (IOException e) {
            FishingBot.getLog().severe("Error while trying to construct: " + packet.getClass().getSimpleName());
        }

        if(getThreshold() > 0) {
            //Send packet (with 0 threshold)
            ByteArrayDataOutput send1 = ByteStreams.newDataOutput();
            PacketHelper.writeVarInt(send1, 0);//do not compress... lol
            send1.write(buf.toByteArray());
            ByteArrayDataOutput send2 = ByteStreams.newDataOutput();
            PacketHelper.writeVarInt(send2, send1.toByteArray().length);
            send2.write(send1.toByteArray());
            try {
                out.write(send2.toByteArray());
                out.flush();
            } catch (IOException e) {
                FishingBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
            }
        } else {
            //Send packet (without threshold)
            ByteArrayDataOutput send = ByteStreams.newDataOutput();
            PacketHelper.writeVarInt(send, buf.toByteArray().length);
            send.write(buf.toByteArray());
            try {
                out.write(send.toByteArray());
                out.flush();
            } catch (IOException e) {
                FishingBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
            }
        }
    }

    public void readData() throws IOException {
        if (getThreshold() > 0) {
            int plen1 = PacketHelper.readVarInt(in);
            int[] dlens = PacketHelper.readVarIntt(in);
            int dlen = dlens[0];
            int plen = plen1 - dlens[1];
            if (dlen == 0) { //this packet isn't compressed
                readUncompressed(plen);
            } else { //this packet is compressed
                readCompressed(plen, dlen);
            }
        } else { //We aren't currently compressing anything
            readUncompressed();
        }

    }

    private void readUncompressed() throws IOException {
        int len1 = PacketHelper.readVarInt(in);
        int[] types = PacketHelper.readVarIntt(in);
        int type = types[0];
        int len = len1 - types[1];
        byte[] data = new byte[len];
        in.readFully(data, 0, len);
        readPacket(len, type, new ByteArrayDataInputWrapper(data));
    }

    private void readUncompressed(int len) throws IOException {
        byte[] data = new byte[len];
        in.readFully(data, 0, len);
        ByteArrayDataInputWrapper bf = new ByteArrayDataInputWrapper(data);
        int type = PacketHelper.readVarInt(bf);
        readPacket(len, type, bf);
    }

    private void readCompressed(int plen, int dlen) throws IOException {
        if (dlen >= getThreshold()) {
            byte[] data = new byte[plen];
            in.readFully(data, 0, plen);
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            byte[] uncompressed = new byte[dlen];
            try {
                inflater.inflate(uncompressed);
            } catch (DataFormatException dataformatexception) {
                dataformatexception.printStackTrace();
                throw new IOException("Bad compressed data format");
            } finally {
                inflater.end();
            }
            ByteArrayDataInputWrapper buf = new ByteArrayDataInputWrapper(uncompressed);
            int type = PacketHelper.readVarInt(buf);
            readPacket(dlen, type, buf);
        } else {
            throw new IOException("Data was smaller than threshold!");
        }
    }

    private void readPacket(int len, int packetId, ByteArrayDataInputWrapper buf) throws IOException {
        Class<? extends Packet> clazz = null;

        switch (state) {
            case HANDSHAKE:
                clazz = getHandshakeRegistry().getPacket(packetId);
                break;
            case LOGIN:
                clazz = getLoginRegistry_IN().getPacket(packetId);
                break;
            case PLAY:
                clazz = getPlayRegistry_IN().getPacket(packetId);
                break;
        }

        if(clazz == null) {
//            System.out.println("Unknown Packet: " + packetId);
            return;
        }

        try {
            Packet packet = clazz.newInstance();
            packet.read(buf, this, len);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
//        FishingBot.getLog().info("RECEIVED PACKET: " + packetId);
//        FishingBot.getLog().info("-> " + Arrays.toString(data) + "\n");
    }

    public void activateEncryption() {
        try {
            out.flush();
            setOutputEncrypted(true);
            BufferedOutputStream var1 = new BufferedOutputStream(CryptManager.encryptOuputStream(getSecretKey(), getSocket().getOutputStream()), 5120);
            this.out = new DataOutputStream(var1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decryptInputStream() {
        setInputBeingDecrypted(true);
        try {
            InputStream var1;
            var1 = getSocket().getInputStream();
            this.in = new DataInputStream(CryptManager.decryptInputStream(getSecretKey(), var1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
