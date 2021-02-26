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
    @Getter private PacketRegistry handshakeRegistry;
    @Getter private PacketRegistry loginRegistryIn;
    @Getter private PacketRegistry loginRegistryOut;
    //List of all PacketRegistries of all supported protocolIds
    @Getter private HashMap<Integer, PacketRegistry> playRegistryIn;
    @Getter private HashMap<Integer, PacketRegistry> playRegistryOut;

    @Getter @Setter private int threshold = -1;
    @Getter @Setter private PublicKey publicKey;
    @Getter @Setter private SecretKey secretKey;
    @Getter @Setter private boolean outputEncrypted;
    @Getter @Setter private boolean inputBeingDecrypted;

    public NetworkHandler() {
        try {
            this.out = new DataOutputStream(FishingBot.getInstance().getCurrentBot().getSocket().getOutputStream());
            this.in = new DataInputStream(FishingBot.getInstance().getCurrentBot().getSocket().getInputStream());

            this.state = State.HANDSHAKE;
            initPacketRegistries();
        } catch (IOException e) {
            e.printStackTrace();
            FishingBot.getI18n().severe("bot-could-not-be-started", e.getMessage());
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
        getLoginRegistryIn().registerPacket(0x04, PacketInLoginPluginRequest.class);

        getLoginRegistryOut().registerPacket(0x00, PacketOutLoginStart.class);
        getLoginRegistryOut().registerPacket(0x01, PacketOutEncryptionResponse.class);
        getLoginRegistryOut().registerPacket(0x02, PacketOutLoginPluginResponse.class);

        //Minecraft 1.8.X

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x02, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x06, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x09, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0E, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x12, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x13, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1C, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1F, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2E, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2F, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x30, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x32, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x38, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x40, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x41, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x46, PacketInSetCompressionLegacy.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x04, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x06, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x09, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0A, PacketOutArmAnimation.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0B, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0D, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0E, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0F, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x15, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x16, PacketOutClientStatus.class);

        //Minecraft 1.9.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x2E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x30, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x37, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x39, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3B, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3D, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3E, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x03, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x05, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x07, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x08, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0B, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0C, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0D, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x14, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x17, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1C, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1D, PacketOutUseItem.class);

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
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x2E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x31, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x39, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3B, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3D, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3F, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x40, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x04, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x06, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x08, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x09, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0B, PacketOutUseEntity.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0C, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0D, PacketOutPlayer.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0E, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0F, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x10, PacketOutLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x15, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1A, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1F, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x20, PacketOutUseItem.class);

        //Minecraft 1.12.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x03, PacketInSpawnMob.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x05, PacketInSpawnPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x2E, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x2F, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x32, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3A, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3C, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3E, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x40, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x41, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x03, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x05, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x07, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x08, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0A, PacketOutUseEntity.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0B, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0C, PacketOutPlayer.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0D, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0E, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0F, PacketOutLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x15, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1A, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1F, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x20, PacketOutUseItem.class);


        //Minecraft 1.12.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1));

        //Minecraft 1.13.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x15, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x30, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x32, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x35, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x43, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x44, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x02, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x03, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x04, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x06, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x08, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x09, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x19, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x10, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x11, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x21, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x29, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x2A, PacketOutUseItem.class);

        //Minecraft 1.13.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x15, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x30, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x35, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x32, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x43, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x44, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13));

        //Minecraft 1.13.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13));

        //Minecraft 1.14.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x33, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x35, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x37, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x43, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x45, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x47, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x48, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x04, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x09, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0A, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0F, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x11, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x12, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x1B, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x23, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x2C, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x2D, PacketOutUseItem.class);

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

        //Minecraft 1.15
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x0E, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x0F, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x13, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x14, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x15, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x26, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x34, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x36, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x38, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x40, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x44, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x46, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x48, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x49, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_15).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.15.1
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_15_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.15.2
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_15_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        //Minecraft 1.16
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x33, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x35, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x37, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x44, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x46, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x48, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x49, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x04, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x09, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x0A, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x10, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x12, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x13, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x24, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x1C, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x2D, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x2E, PacketOutUseItem.class);

        //Minecraft 1.16.1
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16));

        //Minecraft 1.16.2
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x00, PacketInSpawnObject.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x0E, PacketInChat.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x13, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x15, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x19, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x24, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x32, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x34, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x36, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x44, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x46, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x48, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x49, PacketInUpdateHealth.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x03, PacketOutChat.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x04, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x05, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x09, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x0A, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x10, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x12, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x13, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x1C, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x2E, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x2F, PacketOutUseItem.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x3F, PacketOutHeldItemChange.class);

        //Minecraft 1.16.3
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_3).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_3).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2));

        //Minecraft 1.16.4
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_4).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_3));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_4).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_3));

        //Register protocol of latest for unknown versions
        if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(FishingBot.getInstance().getCurrentBot().getServerProtocol())) {
           FishingBot.getI18n().severe("network-not-supported-server-version", FishingBot.getInstance().getCurrentBot().getServerProtocol());

            getPlayRegistryIn().put(FishingBot.getInstance().getCurrentBot().getServerProtocol(), new PacketRegistry());
            getPlayRegistryOut().put(FishingBot.getInstance().getCurrentBot().getServerProtocol(), new PacketRegistry());
            getPlayRegistryIn().get(FishingBot.getInstance().getCurrentBot().getServerProtocol()).copyOf(getPlayRegistryIn().get(ProtocolConstants.getLatest()));
            getPlayRegistryOut().get(FishingBot.getInstance().getCurrentBot().getServerProtocol()).copyOf(getPlayRegistryOut().get(ProtocolConstants.getLatest()));
        }
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
                Packet.writeVarInt(getPlayRegistryOut().get(FishingBot.getInstance().getCurrentBot().getServerProtocol()).getId(packet.getClass()), buf);
                break;
            default:
                return;
        }

        //Add packet payload
        try {
            packet.write(buf, FishingBot.getInstance().getCurrentBot().getServerProtocol());
        } catch (IOException e) {
            FishingBot.getLog().warning("Could not instantiate " + packet.getClass().getSimpleName());
        }

        if (getThreshold() >= 0) {
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
        if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "]  C  >>> |S|: " + packet.getClass().getSimpleName());
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

    private void readPacket(int len, int packetId, ByteArrayDataInputWrapper buf) throws IOException {
        Class<? extends Packet> clazz;

        switch (state) {
            case HANDSHAKE:
                clazz = getHandshakeRegistry().getPacket(packetId);
                break;
            case LOGIN:
                clazz = getLoginRegistryIn().getPacket(packetId);
                break;
            case PLAY:
                clazz = getPlayRegistryIn().get(FishingBot.getInstance().getCurrentBot().getServerProtocol()).getPacket(packetId);
                break;
            default:
                return;
        }

        if (clazz == null) {
            if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets()) {
                byte[] bytes = new byte[buf.getAvailable()];
                buf.readFully(bytes);
                FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] |C| <<<  S : 0x" + Integer.toHexString(packetId));
            }
            return;
        } else if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
            FishingBot.getLog().info("[" + getState().name().toUpperCase() + "] |C| <<<  S : " + clazz.getSimpleName());

        try {
            Packet packet = clazz.newInstance();
            packet.read(buf, this, len, FishingBot.getInstance().getCurrentBot().getServerProtocol());
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
        setInputBeingDecrypted(true);
        try {
            InputStream var1;
            var1 = FishingBot.getInstance().getCurrentBot().getSocket().getInputStream();
            this.in = new DataInputStream(CryptManager.decryptInputStream(getSecretKey(), var1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
