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
import systems.kinau.fishingbot.network.protocol.common.*;
import systems.kinau.fishingbot.network.protocol.configuration.*;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentRegistry;
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

    @Getter @Setter private DataComponentRegistry dataComponentRegistry;

    @Getter private DataOutputStream out;
    @Getter private DataInputStream in;

    @Getter @Setter private State state;
    @Getter private PacketRegistry handshakeRegistry;
    @Getter private PacketRegistry loginRegistryIn;
    @Getter private PacketRegistry loginRegistryOut;
    @Getter private HashMap<Integer, PacketRegistry> configurationRegistryIn;
    @Getter private HashMap<Integer, PacketRegistry> configurationRegistryOut;
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
            this.dataComponentRegistry = new DataComponentRegistry();
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
        this.configurationRegistryIn = new HashMap<>();
        this.configurationRegistryOut = new HashMap<>();
        this.playRegistryIn = new HashMap<>();
        this.playRegistryOut = new HashMap<>();

        ProtocolConstants.SUPPORTED_VERSION_IDS.forEach(protId -> {
            configurationRegistryIn.put(protId, new PacketRegistry());
            configurationRegistryOut.put(protId, new PacketRegistry());
            playRegistryIn.put(protId, new PacketRegistry());
            playRegistryOut.put(protId, new PacketRegistry());
        });

        // All versions

        getHandshakeRegistry().registerPacket(0x00, PacketOutHandshake.class);

        getLoginRegistryIn().registerPacket(0x00, PacketInLoginDisconnect.class);
        getLoginRegistryIn().registerPacket(0x01, PacketInEncryptionRequest.class);
        getLoginRegistryIn().registerPacket(0x02, PacketInLoginSuccess.class);
        getLoginRegistryIn().registerPacket(0x03, PacketInSetCompression.class);
        getLoginRegistryIn().registerPacket(0x04, PacketInLoginPluginRequest.class);

        getLoginRegistryOut().registerPacket(0x00, PacketOutLoginStart.class);
        getLoginRegistryOut().registerPacket(0x01, PacketOutEncryptionResponse.class);
        getLoginRegistryOut().registerPacket(0x02, PacketOutLoginPluginResponse.class);
        getLoginRegistryOut().registerPacket(0x03, PacketOutLoginAcknowledge.class); // since 1.20.2 (switches to configuration state)

        // Minecraft 1.8.X

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x02, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x06, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x08, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x09, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x0E, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x12, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x13, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x15, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x17, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x18, PacketInEntityTeleport.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1C, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x1F, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2D, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2E, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x2F, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x30, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x32, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x38, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x40, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x41, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x46, PacketInSetCompressionLegacy.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x00, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_8).registerPacket(0x01, PacketOutChatMessage.class);
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

        // Minecraft 1.9.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x13, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x25, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x26, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x2E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x30, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x37, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x39, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3B, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3D, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x3E, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x4A, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9).registerPacket(0x02, PacketOutChatMessage.class);
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

        // Minecraft 1.9.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        // Minecraft 1.9.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        // Minecraft 1.9.4

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x13, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x25, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x26, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x2E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x30, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x37, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x39, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x3B, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x3D, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x3E, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4).registerPacket(0x49, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_4).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9));

        // Minecraft 1.10.X

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_10).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_10).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_4));

        // Minecraft 1.11.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_11).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_11).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_4));

        // Minecraft 1.11.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_11_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_9_4));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_11_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_9_4));

        // Minecraft 1.12.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x03, PacketInSpawnMob.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x05, PacketInSpawnPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x13, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x26, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x27, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x2D, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x2E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x31, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x39, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3B, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3D, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x3F, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x40, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x4B, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12).registerPacket(0x03, PacketOutChatMessage.class);
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

        // Minecraft 1.12.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x03, PacketInSpawnMob.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x05, PacketInSpawnPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x13, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x26, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x27, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x2E, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x2F, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x32, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3A, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3C, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x3E, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x40, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x41, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x4C, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1).registerPacket(0x02, PacketOutChatMessage.class);
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


        // Minecraft 1.12.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_12_1));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_12_1));

        // Minecraft 1.13.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x0E, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x14, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x15, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x28, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x29, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x30, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x32, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x35, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x43, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x44, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x50, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13).registerPacket(0x02, PacketOutChatMessage.class);
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

        // Minecraft 1.13.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x0E, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x14, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x15, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x28, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x29, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x30, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x35, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x32, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x3F, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x41, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x43, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x44, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1).registerPacket(0x50, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13));

        // Minecraft 1.13.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_13_1));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_13));

        // Minecraft 1.14.0

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x0E, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x28, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x29, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x2E, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x33, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x35, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x37, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x43, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x45, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x47, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x48, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x56, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14).registerPacket(0x03, PacketOutChatMessage.class);
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

        // Minecraft 1.14.1

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.14.2

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.14.3

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_3).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_3).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.14.4

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14_4).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_14));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14_4).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.15
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x0E, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x13, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x14, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x15, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x17, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x26, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x28, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x29, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x2F, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x34, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x36, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x38, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x40, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x44, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x46, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x48, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x49, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15).registerPacket(0x57, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_15).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.15.1
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_15_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.15.2
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_15));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_15_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_14));

        // Minecraft 1.16
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x0E, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x12, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x28, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x29, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x2E, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x33, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x35, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x37, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x44, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x46, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x48, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x49, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x56, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16).registerPacket(0x03, PacketOutChatMessage.class);
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

        // Minecraft 1.16.1
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16));

        // Minecraft 1.16.2
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x0D, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x0E, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x11, PacketInConfirmTransaction.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x13, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x15, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x19, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x24, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x27, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x28, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x2D, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x32, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x34, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x36, PacketInDestroyEntities.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x3F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x44, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x46, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x48, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x49, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x56, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2).registerPacket(0x03, PacketOutChatMessage.class);
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

        // Minecraft 1.16.3
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_3).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_2));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_3).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_2));

        // Minecraft 1.16.4
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_4).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_16_3));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_4).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_16_3));

        // Minecraft 1.17
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x0E, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x26, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x29, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x2A, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x2E, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x36, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x38, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x48, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x4D, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x4F, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x51, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x52, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x61, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x03, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x04, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x05, PacketOutClientSettings.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x08, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x09, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x0F, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x11, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x12, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x1B, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x25, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x2E, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17).registerPacket(0x2F, PacketOutUseItem.class);

        // Minecraft 1.17.1
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17_1).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_17));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17_1).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17));

        // Minecraft 1.18
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x0E, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x0F, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x13, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x14, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x16, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x21, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x26, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x29, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x2A, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x2E, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x36, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x38, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x48, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x4D, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x4F, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x51, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x52, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18).registerPacket(0x62, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_18).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_17));

        // Minecraft 1.18.2
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18_2).copyOf(getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_18));
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_18_2).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_18));

        // Minecraft 1.19
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x0B, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x10, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x11, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x13, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x17, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x1E, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x23, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x26, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x27, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x2B, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x30, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x34, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x36, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x47, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x4D, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x4F, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x51, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x52, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x5F, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x63, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x03, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x04, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x06, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x07, PacketOutClientSettings.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x0A, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x0B, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x11, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x13, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x14, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x1D, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x25, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x30, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19).registerPacket(0x31, PacketOutUseItem.class);

        // Minecraft 1.19.1
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x0B, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x0F, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x10, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x11, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x13, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x19, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x20, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x25, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x28, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x29, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x2D, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x33, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x37, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x39, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x4A, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x50, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x52, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x54, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x55, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x62, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x66, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x04, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x05, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x07, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x08, PacketOutClientSettings.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x0B, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x0C, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x12, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x14, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x15, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x1E, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x28, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x31, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_1).registerPacket(0x32, PacketOutUseItem.class);

        // Minecraft 1.19.3
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x00, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x0B, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x0E, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x0F, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x10, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x12, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x17, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x1F, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x24, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x27, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x28, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x2C, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x31, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x35, PacketInPlayerListItemRemove.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x36, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x38, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x49, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x4E, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x50, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x52, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x53, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x60, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x64, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x04, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x05, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x06, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x07, PacketOutClientSettings.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x0A, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x0B, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x11, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x13, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x14, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x1D, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x20, PacketOutChatSessionUpdate.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x28, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x31, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_3).registerPacket(0x32, PacketOutUseItem.class);

        // Minecraft 1.19.4
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x01, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x0C, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x10, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x11, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x12, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x14, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x23, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x28, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x2B, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x2C, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x30, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x35, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x39, PacketInPlayerListItemRemove.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x3A, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x3C, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x4D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x52, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x54, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x56, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x57, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x64, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x68, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x04, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x05, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x06, PacketOutChatSessionUpdate.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x07, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x08, PacketOutClientSettings.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x0B, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x0C, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x12, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x14, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x15, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x1E, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x28, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x31, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4).registerPacket(0x32, PacketOutUseItem.class);

        // Minecraft 1.20
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x01, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x0C, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x10, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x11, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x12, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x14, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x1A, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x23, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x28, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x2B, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x2C, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x30, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x35, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x39, PacketInPlayerListItemRemove.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x3A, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x3C, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x4D, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x52, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x54, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x56, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x57, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x64, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20).registerPacket(0x68, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20).copyOf(getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_19_4));

        // Minecraft 1.20.2
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x02, PacketInFinishConfiguration.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x03, PacketInKeepAlive.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x04, PacketInPing.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x06, PacketInResourcePack.class);

        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x01, PacketOutPluginMessage.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x02, PacketOutFinishConfiguration.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x03, PacketOutKeepAlive.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x04, PacketOutPing.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x05, PacketOutResourcePackResponse.class);

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x01, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x0B, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x11, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x13, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x15, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x24, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x29, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x2C, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x2D, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x31, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x37, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x3B, PacketInPlayerListItemRemove.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x3C, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x3E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x42, PacketInResourcePack.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x4F, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x54, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x56, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x58, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x59, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x65, PacketInStartConfiguration.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x67, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x6B, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x04, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x05, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x06, PacketOutChatSessionUpdate.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x08, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x09, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x0B, PacketOutAcknowledgeConfiguration.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x0D, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x0E, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x0F, PacketOutPluginMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x14, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x16, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x17, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x21, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x27, PacketOutResourcePackResponse.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x2B, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x34, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x35, PacketOutUseItem.class);

        // Minecraft 1.20.3
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x02, PacketInFinishConfiguration.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x03, PacketInKeepAlive.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x04, PacketInPing.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x07, PacketInResourcePack.class);

        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).copyOf(getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2));

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x01, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x0B, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x11, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x13, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x15, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x1B, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x24, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x29, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x2C, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x2D, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x31, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x37, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x3B, PacketInPlayerListItemRemove.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x3C, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x3E, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x44, PacketInResourcePack.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x51, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x56, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x58, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x5A, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x5B, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x67, PacketInStartConfiguration.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x69, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x6D, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x04, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x05, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x06, PacketOutChatSessionUpdate.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x08, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x09, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x0B, PacketOutAcknowledgeConfiguration.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x0D, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x0E, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x10, PacketOutPluginMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x15, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x17, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x18, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x21, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x28, PacketOutResourcePackResponse.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x2C, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x35, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_3).registerPacket(0x36, PacketOutUseItem.class);

        // Minecraft 1.20.5
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x03, PacketInFinishConfiguration.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x04, PacketInKeepAlive.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x05, PacketInPing.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x09, PacketInResourcePack.class);
        getConfigurationRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x0E, PacketInKnownPacks.class);

        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x02, PacketOutPluginMessage.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x03, PacketOutFinishConfiguration.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x04, PacketOutKeepAlive.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x05, PacketOutPing.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x06, PacketOutResourcePackResponse.class);
        getConfigurationRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x07, PacketOutKnownPacks.class);

        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x01, PacketInSpawnEntity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x0B, PacketInDifficultySet.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x11, PacketInCommands.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x12, PacketInWindowClose.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x13, PacketInWindowItems.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x15, PacketInSetSlot.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x1D, PacketInDisconnect.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x26, PacketInKeepAlive.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x2B, PacketInJoinGame.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x2E, PacketInEntityPosition.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x2F, PacketInEntityPositionRotation.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x33, PacketInOpenWindow.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x39, PacketInChatPlayer.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x3D, PacketInPlayerListItemRemove.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x3E, PacketInPlayerListItem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x40, PacketInPlayerPosLook.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x46, PacketInResourcePack.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x53, PacketInHeldItemChange.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x58, PacketInEntityMetadata.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x5A, PacketInEntityVelocity.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x5C, PacketInSetExperience.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x5D, PacketInUpdateHealth.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x69, PacketInStartConfiguration.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x6C, PacketInChatSystem.class);
        getPlayRegistryIn().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x70, PacketInEntityTeleport.class);

        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x00, PacketOutTeleportConfirm.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x04, PacketOutUnsignedChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x05, PacketOutChatCommand.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x06, PacketOutChatMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x07, PacketOutChatSessionUpdate.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x09, PacketOutClientStatus.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x0A, PacketOutClientSettings.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x0C, PacketOutAcknowledgeConfiguration.class);
//        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_2).registerPacket(0x07, PacketOutConfirmTransaction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x0E, PacketOutClickWindow.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x0F, PacketOutCloseInventory.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x12, PacketOutPluginMessage.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x18, PacketOutKeepAlive.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x1A, PacketOutPosition.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x1B, PacketOutPosLook.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x24, PacketOutEntityAction.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x2B, PacketOutResourcePackResponse.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x2F, PacketOutHeldItemChange.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x38, PacketOutBlockPlace.class);
        getPlayRegistryOut().get(ProtocolConstants.MINECRAFT_1_20_5_RC_3).registerPacket(0x39, PacketOutUseItem.class);


        // Register protocol of latest for unknown versions
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

        // Add Packet ID from serverProtocol-specific PacketRegistry
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
            case CONFIGURATION:
                Packet.writeVarInt(getConfigurationRegistryOut().get(FishingBot.getInstance().getCurrentBot().getServerProtocol()).getId(packet.getClass()), buf);
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
            // Send packet (with 0 threshold, no compression)
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
            // Send packet (without threshold)
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
        Class<? extends Packet> clazz = null;

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
            case CONFIGURATION:
                clazz = getConfigurationRegistryIn().get(FishingBot.getInstance().getCurrentBot().getServerProtocol()).getPacket(packetId);
                break;
            default:
                break;
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

    public boolean isEncrypted() {
        return outputEncrypted;
    }
}
