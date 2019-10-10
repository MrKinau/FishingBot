/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.handshake.PacketHandshake;
import systems.kinau.fishingbot.network.protocol.login.*;
import systems.kinau.fishingbot.network.protocol.play.*;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class NetworkHandler {

    @Getter private AuthData authData;

    @Getter private Socket socket;
    @Getter private DataOutputStream out;
    @Getter private DataInputStream in;

    @Getter @Setter private State state;
    @Getter private PacketRegistry handshakeRegistry, loginRegistry_IN, loginRegistry_OUT;
    //List of all PacketRegistries of all supported protocolIds
    @Getter private HashMap<Integer, PacketRegistry> playRegistry_IN, playRegistry_OUT;

    @Getter @Setter private int threshold = 0;
    @Getter @Setter PublicKey publicKey;
    @Getter @Setter SecretKey secretKey;
    @Getter @Setter private boolean outputEncrypted;
    @Getter @Setter private boolean inputBeingDecrypted;

    public NetworkHandler(Socket socket) {
        this.socket = socket;
        this.authData = MineBot.getInstance().getAuthData();
        MineBot.getInstance().getManager().setNetworkHandler(this);
        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());

            this.state = State.HANDSHAKE;
            initPacketRegistries();
        } catch (IOException e) {
            MineBot.getLog().severe("Could not start bot: " + e.getMessage());
        }
    }

    private void initPacketRegistries() {
        this.handshakeRegistry = new PacketRegistry();
        this.loginRegistry_IN = new PacketRegistry();
        this.loginRegistry_OUT = new PacketRegistry();
        this.playRegistry_IN = new HashMap<>();
        this.playRegistry_OUT = new HashMap<>();

        ProtocolConstants.SUPPORTED_VERSION_IDS.forEach(protId -> {
            playRegistry_IN.put(protId, new PacketRegistry());
            playRegistry_OUT.put(protId, new PacketRegistry());
        });

        //All versions

        getHandshakeRegistry().registerPacket(0x00, PacketHandshake.class);

        getLoginRegistry_IN().registerPacket(0x00, PacketInLoginDisconnect.class);
        getLoginRegistry_IN().registerPacket(0x01, PacketInEncryptionRequest.class);
        getLoginRegistry_IN().registerPacket(0x02, PacketInLoginSuccess.class);
        getLoginRegistry_IN().registerPacket(0x03, PacketInSetCompression.class);

        getLoginRegistry_OUT().registerPacket(0x00, PacketOutLoginStart.class);
        getLoginRegistry_OUT().registerPacket(0x01, PacketOutEncryptionResponse.class);

        //Minecraft 1.8.X

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0E, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x41, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x40, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x12, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1C, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x09, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2F, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketInPlayerPosLook.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x02, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1F, PacketInSetExperience.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x21, PacketInChunk.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x26, PacketInChunkBulk.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketOutChat.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x15, PacketOutClientSettings.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketOutUseItem.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0A, PacketOutArmAnimation.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x04, PacketOutPosition.class);

        //Minecraft 1.9.0

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3B, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x39, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x37, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3D, PacketInSetExperience.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0B, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1D, PacketOutUseItem.class);

        //Minecraft 1.9.1

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9_1).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9_1).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.9.2

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9_2).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9_2).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.9.4

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9_4).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9_4).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.10.X

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_10).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_10).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.11.0

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_11).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_11).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.11.1

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_11_1).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_11_1).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.12.0

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3D, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3B, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x39, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3F, PacketInSetExperience.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0C, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x20, PacketOutUseItem.class);

        //Minecraft 1.12.1

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3E, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3C, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3A, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x40, PacketInSetExperience.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0B, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x20, PacketOutUseItem.class);

        //Minecraft 1.12.2

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_12_1));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_12_1));

        //Minecraft 1.13.0

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x43, PacketInSetExperience.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x2A, PacketOutUseItem.class);

        //Minecraft 1.13.1

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x43, PacketInSetExperience.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13_1).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13));

        //Minecraft 1.13.2

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_13_1));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_13));

        //Minecraft 1.14.0

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x45, PacketInEntityVelocity.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x43, PacketInEntityMetadata.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x47, PacketInSetExperience.class);

        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0F, PacketOutKeepAlive.class);
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x2D, PacketOutUseItem.class);

        //Minecraft 1.14.1

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14_1).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14_1).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.14.2

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14_2).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14_2).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.14.3

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14_3).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14_3).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.14.4

        getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14_4).copyOf(getPlayRegistry_IN().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14_4).copyOf(getPlayRegistry_OUT().get(ProtocolConstants.MINECRAFT_1_14));
    }

    public void sendPacket(Packet packet) {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();

        //Add Packet ID from serverProtocol-specific PacketRegistry
        switch (getState()) {
            case HANDSHAKE:
                Packet.writeVarInt(getHandshakeRegistry().getId(packet.getClass()), buf);
                break;
            case LOGIN:
                Packet.writeVarInt(getLoginRegistry_OUT().getId(packet.getClass()), buf);
                break;
            case PLAY:
                Packet.writeVarInt(getPlayRegistry_OUT().get(MineBot.getServerProtocol()).getId(packet.getClass()), buf);
                break;
        }

        //Add packet payload
        try {
            packet.write(buf, MineBot.getServerProtocol());
        } catch (IOException e) {
            MineBot.getLog().warning("Could not instantiate " + packet.getClass().getSimpleName());
        }

        if (getThreshold() > 0) {
            //Send packet (with 0 threshold, no compression)
            ByteArrayDataOutput send1 = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, send1);
            send1.write(buf.toByteArray());
            ByteArrayDataOutput send2 = ByteStreams.newDataOutput();
            Packet.writeVarInt(send1.toByteArray().length, send2);
            send2.write(send1.toByteArray());
            try {
                out.write(send2.toByteArray());
                out.flush();
            } catch (IOException e) {
                MineBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
            }
        } else {
            //Send packet (without threshold)
            ByteArrayDataOutput send = ByteStreams.newDataOutput();
            Packet.writeVarInt(buf.toByteArray().length, send);
            send.write(buf.toByteArray());
            try {
                out.write(send.toByteArray());
                out.flush();
            } catch (IOException e) {
                MineBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
//        FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] C >>> S: " + packet.getClass().getSimpleName());
    }

    public void readData() throws IOException {
        if (getThreshold() > 0) {
            int plen1 = Packet.readVarInt(in);
            int[] dlens = Packet.readVarIntt(in);
            int dlen = dlens[0];
            int plen = plen1 - dlens[1];
            if (dlen == 0) {
                readUncompressed(plen);
            } else {
                readCompressed(plen, dlen);
            }
        } else {
            readUncompressed();
        }

    }

    private void readUncompressed() throws IOException {
        int len1 = Packet.readVarInt(in);
        int[] types = Packet.readVarIntt(in);
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
        int type = Packet.readVarInt(bf);
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
            int type = Packet.readVarInt(buf);
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
                clazz = getPlayRegistry_IN().get(MineBot.getServerProtocol()).getPacket(packetId);
                break;
        }

        if (clazz == null) {
//            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] C <<< S: 0x" + Integer.toHexString(packetId));
            return;
        }
//        FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] C <<< S: " + clazz.getSimpleName());

        try {
            Packet packet = clazz.newInstance();
            packet.read(buf, this, len, MineBot.getServerProtocol());
        } catch (InstantiationException | IllegalAccessException e) {
            MineBot.getLog().warning("Could not create new instance of " + clazz.getSimpleName());
            e.printStackTrace();
        }
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
