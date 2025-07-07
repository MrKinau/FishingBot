/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.entity.EntityDataParser;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentRegistry;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;
import systems.kinau.fishingbot.network.utils.InvalidPacketException;

import javax.crypto.SecretKey;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Getter
public class NetworkHandler {

    private DataComponentRegistry dataComponentRegistry;
    private EntityDataParser entityDataParser;

    private DataOutputStream out;
    private DataInputStream in;

    @Setter private ProtocolState state;
    private PacketRegistry handshakeRegistry;
    private PacketRegistry loginRegistryIn;
    private PacketRegistry loginRegistryOut;
    private PacketRegistry configurationRegistryIn;
    private PacketRegistry configurationRegistryOut;
    private PacketRegistry playRegistryIn;
    private PacketRegistry playRegistryOut;

    @Setter private int threshold = -1;
    @Setter private PublicKey publicKey;
    @Setter private SecretKey secretKey;
    @Setter private boolean outputEncrypted;

    public NetworkHandler() {
        try {
            this.out = new DataOutputStream(FishingBot.getInstance().getCurrentBot().getSocket().getOutputStream());
            this.in = new DataInputStream(FishingBot.getInstance().getCurrentBot().getSocket().getInputStream());

            this.state = ProtocolState.HANDSHAKE;
            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_20_5)
                this.dataComponentRegistry = new DataComponentRegistry();
            this.entityDataParser = new EntityDataParser();
            initPacketRegistries();
        } catch (IOException e) {
            e.printStackTrace();
            FishingBot.getI18n().severe("bot-could-not-be-started", e.getMessage());
        }
    }

    private void initPacketRegistries() {
        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();

        this.handshakeRegistry = new PacketRegistry(protocolId, ProtocolState.HANDSHAKE, ProtocolFlow.OUTGOING_PACKET);
        this.loginRegistryIn = new PacketRegistry(protocolId, ProtocolState.LOGIN, ProtocolFlow.INCOMING_PACKET);
        this.loginRegistryOut = new PacketRegistry(protocolId, ProtocolState.LOGIN, ProtocolFlow.OUTGOING_PACKET);
        this.configurationRegistryIn = new PacketRegistry(protocolId, ProtocolState.CONFIGURATION, ProtocolFlow.INCOMING_PACKET);
        this.configurationRegistryOut = new PacketRegistry(protocolId, ProtocolState.CONFIGURATION, ProtocolFlow.OUTGOING_PACKET);
        this.playRegistryIn = new PacketRegistry(protocolId, ProtocolState.PLAY, ProtocolFlow.INCOMING_PACKET);
        this.playRegistryOut = new PacketRegistry(protocolId, ProtocolState.PLAY, ProtocolFlow.OUTGOING_PACKET);
    }

    public void sendPacket(Packet packet) {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();

        // Add Packet ID from serverProtocol-specific PacketRegistry
        switch (getState()) {
            case HANDSHAKE:
                Packet.writeVarInt(getHandshakeRegistry().getId(packet.getClass()), buf);
                break;
            case LOGIN:
                Packet.writeVarInt(getLoginRegistryOut().getId(packet.getClass()), buf);
                break;
            case PLAY:
                Packet.writeVarInt(getPlayRegistryOut().getId(packet.getClass()), buf);
                break;
            case CONFIGURATION:
                try {
                    Packet.writeVarInt(getConfigurationRegistryOut().getId(packet.getClass()), buf);
                } catch (InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                return;
        }

        // Add packet payload
        try {
            packet.write(buf, FishingBot.getInstance().getCurrentBot().getServerProtocol());
        } catch (IOException e) {
            FishingBot.getLog().warning("Could not instantiate " + packet.getClass().getSimpleName());
        }

        if (getThreshold() >= 0) {
            try {
                sendCompressed(buf);
            } catch (IOException e) {
                FishingBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
                e.printStackTrace();
            }
        } else {
            try {
                sendUncompressed(buf);
            } catch (IOException e) {
                FishingBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "]  C  >>> |S|: " + packet.getClass().getSimpleName());
    }

    private void sendCompressed(ByteArrayDataOutput buf) throws IOException {
        int length = buf.toByteArray().length;

        if (length < getThreshold()) {
            ByteArrayDataOutput compressedBuf = ByteStreams.newDataOutput();
            Packet.writeVarInt(0, compressedBuf);
            compressedBuf.write(buf.toByteArray());

            ByteArrayDataOutput send = prependLength(compressedBuf);

            out.write(send.toByteArray());
        } else {
            ByteArrayDataOutput compressedBuf = compress(buf, length);

            ByteArrayDataOutput send = prependLength(compressedBuf);

            out.write(send.toByteArray());
        }

        out.flush();
    }

    private ByteArrayDataOutput compress(ByteArrayDataOutput buf, int length) throws IOException {
        ByteArrayDataOutput compressedBuf = ByteStreams.newDataOutput();
        Packet.writeVarInt(length, compressedBuf);
        Deflater deflater = new Deflater();
        deflater.setInput(buf.toByteArray());
        deflater.finish();

        byte[] encodeBuf = new byte[8192];
        while (!deflater.finished()) {
            int len = deflater.deflate(encodeBuf);
            compressedBuf.write(encodeBuf, 0, len);
        }

        deflater.reset();
        return compressedBuf;
    }

    private void sendUncompressed(ByteArrayDataOutput buf) throws IOException {
        ByteArrayDataOutput send = prependLength(buf);
        out.write(send.toByteArray());
        out.flush();
    }

    private ByteArrayDataOutput prependLength(ByteArrayDataOutput buf) {
        ByteArrayDataOutput lengthPrepended = ByteStreams.newDataOutput();
        Packet.writeVarInt(buf.toByteArray().length, lengthPrepended);
        lengthPrepended.write(buf.toByteArray());
        return lengthPrepended;
    }

    public void readData() throws IOException {
        if (getThreshold() >= 0) {
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

    private PacketRegistry getCurrentPacketRegistry() {
        switch (state) {
            case HANDSHAKE: return getHandshakeRegistry();
            case LOGIN: return getLoginRegistryIn();
            case PLAY: return getPlayRegistryIn();
            case CONFIGURATION: return getConfigurationRegistryIn();
        }
        return null;
    }

    private void readPacket(int len, int packetId, ByteArrayDataInputWrapper buf) throws IOException {
        PacketRegistry packetRegistry = getCurrentPacketRegistry();
        Class<? extends Packet> clazz = packetRegistry == null ? null : packetRegistry.getPacket(packetId);

        if (clazz == null) {
            if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets()) {
                byte[] bytes = new byte[buf.getAvailable()];
                buf.readFully(bytes);
                FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] |C| <<<  S : 0x" + Integer.toHexString(packetId) + " (" + (packetRegistry != null ? packetRegistry.getMojMapPacketName(packetId) : "") + ")");
            }
            return;
        } else if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] |C| <<<  S : " + clazz.getSimpleName());

        try {
            long startTime = System.currentTimeMillis();

            Packet packet = clazz.newInstance();
            packet.read(buf, this, len, FishingBot.getInstance().getCurrentBot().getServerProtocol());

            long endTime = System.currentTimeMillis();

            if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets() && endTime - startTime > 2)
                FishingBot.getLog().info("Handling packet " + clazz.getSimpleName() + " took " + (endTime - startTime) + "ms");
        } catch (InstantiationException | IllegalAccessException e) {
            FishingBot.getLog().warning("Could not create new instance of " + clazz.getSimpleName());
            e.printStackTrace();
        }
    }

    public void activateEncryption() {
        try {
            out.flush();
            setOutputEncrypted(true);
            BufferedOutputStream var1 = new BufferedOutputStream(CryptManager.encryptOuputStream(getSecretKey(), FishingBot.getInstance().getCurrentBot().getSocket().getOutputStream()), 5120);
            this.out = new DataOutputStream(var1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decryptInputStream() {
        try {
            InputStream var1;
            var1 = FishingBot.getInstance().getCurrentBot().getSocket().getInputStream();
            this.in = new DataInputStream(CryptManager.decryptInputStream(getSecretKey(), var1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEncrypted() {
        return outputEncrypted;
    }
}
