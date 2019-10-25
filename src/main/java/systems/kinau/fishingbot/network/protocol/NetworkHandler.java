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
import systems.kinau.fishingbot.network.protocol.handshake.PacketOutHandshake;
import systems.kinau.fishingbot.network.protocol.login.*;
import systems.kinau.fishingbot.network.protocol.play.*;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class NetworkHandler {

    @Getter private DataOutputStream out;
    @Getter private DataInputStream in;

    @Getter @Setter private State state;
    @Getter private PacketRegistry handshakeRegistry, loginRegistryIn, loginRegistryOut;
    //List of all PacketRegistries of all supported protocolIds
    @Getter private HashMap<Integer, PacketRegistry> playRegistryIn, playRegistryOut;

    @Getter @Setter private int threshold = 0;
    @Getter @Setter PublicKey publicKey;
    @Getter @Setter SecretKey secretKey;
    @Getter @Setter private boolean outputEncrypted;
    @Getter @Setter private boolean inputBeingDecrypted;

    public NetworkHandler() {
        try {
            this.out = new DataOutputStream(FishingBot.getInstance().getSocket().getOutputStream());
            this.in = new DataInputStream(FishingBot.getInstance().getSocket().getInputStream());

            this.state = State.HANDSHAKE;
            initPacketRegistries();
        } catch (IOException e) {
            FishingBot.getLog().severe("Could not start bot: " + e.getMessage());
        }
    }

    private void initPacketRegistries() {
        this.handshakeRegistry = new PacketRegistry();
        this.loginRegistryIn = new PacketRegistry();
        this.loginRegistryOut = new PacketRegistry();
        this.playRegistryIn = new HashMap<>();
        this.playRegistryOut = new HashMap<>();

        ProtocolConstants.SUPPORTED_VERSION_IDS.forEach(protId -> {
            playRegistryIn.put(protId, new PacketRegistry());
            playRegistryOut.put(protId, new PacketRegistry());
        });

        //All versions

        getHandshakeRegistry().registerPacket(0x00, PacketOutHandshake.class);

        getLoginRegistryIn().registerPacket(0x00, PacketInLoginDisconnect.class);
        getLoginRegistryIn().registerPacket(0x01, PacketInEncryptionRequest.class);
        getLoginRegistryIn().registerPacket(0x02, PacketInLoginSuccess.class);
        getLoginRegistryIn().registerPacket(0x03, PacketInSetCompression.class);

        getLoginRegistryOut().registerPacket(0x00, PacketOutLoginStart.class);
        getLoginRegistryOut().registerPacket(0x01, PacketOutEncryptionResponse.class);

        //Minecraft 1.8.X

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0E, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x41, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x40, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x12, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1C, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x09, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2F, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x02, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1F, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x38, PacketInPlayerListItem.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x15, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0A, PacketOutArmAnimation.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x04, PacketOutPosition.class);

        //Minecraft 1.9.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3B, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x39, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x37, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3D, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x2E, PacketInPlayerPosLook.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0B, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1D, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0C, PacketOutPosition.class);

        //Minecraft 1.9.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.9.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.9.4

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_4).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.10.X

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_10).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_10).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.11.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_11).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_11).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.11.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_11_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_11_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        //Minecraft 1.12.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x03, PacketInSpawnMob.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x05, PacketInSpawnPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3D, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3B, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x39, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3F, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x2E, PacketInPlayerPosLook.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0B, PacketOutUseEntity.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0C, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x20, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0D, PacketOutPlayer.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0E, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0F, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x10, PacketOutLook.class);

        //Minecraft 1.12.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x03, PacketInSpawnMob.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x05, PacketInSpawnPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3E, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3C, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3A, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x40, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x2E, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x2F, PacketInPlayerPosLook.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0A, PacketOutUseEntity.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0B, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x20, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0C, PacketOutPlayer.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0D, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0E, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0F, PacketOutLook.class);


        //Minecraft 1.12.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1));

        //Minecraft 1.13.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x43, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x30, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x32, PacketInPlayerPosLook.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x2A, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x10, PacketOutPosition.class);

        //Minecraft 1.13.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x43, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x30, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x32, PacketInPlayerPosLook.class);


        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13));

        //Minecraft 1.13.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13));

        //Minecraft 1.14.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x45, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x43, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x47, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x33, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x35, PacketInPlayerPosLook.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0F, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x2D, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x11, PacketOutPosition.class);

        //Minecraft 1.14.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.14.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.14.3

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_3).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_3).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.14.4

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_4).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_4).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));
    }

    public void sendPacket(Packet packet) {
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();

        //Add Packet ID from serverProtocol-specific PacketRegistry
        switch (getState()) {
            case HANDSHAKE:
                Packet.writeVarInt(getHandshakeRegistry().getId(packet.getClass()), buf);
                break;
            case LOGIN:
                Packet.writeVarInt(getLoginRegistryOut().getId(packet.getClass()), buf);
                break;
            case PLAY:
                Packet.writeVarInt(getPlayRegistryOut().get(FishingBot.getInstance().getServerProtocol()).getId(packet.getClass()), buf);
                break;
        }

        //Add packet payload
        try {
            packet.write(buf, FishingBot.getInstance().getServerProtocol());
        } catch (IOException e) {
            FishingBot.getLog().warning("Could not instantiate " + packet.getClass().getSimpleName());
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
                FishingBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
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
                FishingBot.getLog().severe("Error while trying to send: " + packet.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        if (FishingBot.getInstance().getConfig().isLogPackets())
            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] C >>> S: " + packet.getClass().getSimpleName());
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
                clazz = getLoginRegistryIn().getPacket(packetId);
                break;
            case PLAY:
                clazz = getPlayRegistryIn().get(FishingBot.getInstance().getServerProtocol()).getPacket(packetId);
                break;
        }

        if (clazz == null) {
            if (FishingBot.getInstance().getConfig().isLogPackets())
                FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] C <<< S: 0x" + Integer.toHexString(packetId));
            return;
        } else if (FishingBot.getInstance().getConfig().isLogPackets())
            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] C <<< S: " + clazz.getSimpleName());

        try {
            Packet packet = clazz.newInstance();
            packet.read(buf, this, len, FishingBot.getInstance().getServerProtocol());
        } catch (InstantiationException | IllegalAccessException e) {
            FishingBot.getLog().warning("Could not create new instance of " + clazz.getSimpleName());
            e.printStackTrace();
        }
    }

    public void activateEncryption() {
        try {
            out.flush();
            setOutputEncrypted(true);
            BufferedOutputStream var1 = new BufferedOutputStream(CryptManager.encryptOuputStream(getSecretKey(), FishingBot.getInstance().getSocket().getOutputStream()), 5120);
            this.out = new DataOutputStream(var1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decryptInputStream() {
        setInputBeingDecrypted(true);
        try {
            InputStream var1;
            var1 = FishingBot.getInstance().getSocket().getInputStream();
            this.in = new DataInputStream(CryptManager.decryptInputStream(getSecretKey(), var1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
