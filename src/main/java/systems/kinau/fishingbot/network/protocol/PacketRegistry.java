/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.common.*;
import systems.kinau.fishingbot.network.protocol.configuration.*;
import systems.kinau.fishingbot.network.protocol.handshake.PacketOutHandshake;
import systems.kinau.fishingbot.network.protocol.login.*;
import systems.kinau.fishingbot.network.protocol.play.*;
import systems.kinau.fishingbot.network.utils.InvalidPacketException;

import java.io.InputStreamReader;

@Getter
public class PacketRegistry {

    private final int protocolId;
    private final ProtocolState state;
    private final ProtocolFlow flow;
    private final JsonParser parser = new JsonParser();

    private final BiMap<Integer, Class<? extends Packet>> registeredPackets = HashBiMap.create();

    public PacketRegistry(int protocolId, ProtocolState state, ProtocolFlow flow) {
        this.protocolId = protocolId;
        this.state = state;
        this.flow = flow;
        JsonObject data = loadBundledPacketRegistry(protocolId);
        if (data == null) {
            protocolId = ProtocolConstants.getLatest();
            data = loadBundledPacketRegistry(protocolId);
        }
        if (data == null) throw new IllegalArgumentException("Could not load bundled packets for " + ProtocolConstants.getVersionString(protocolId));
        JsonObject stateObj = data.getAsJsonObject(state.getId());
        if (stateObj == null) throw new IllegalArgumentException("Could not load bundled packets for " + state.getId() + "/" + ProtocolConstants.getVersionString(protocolId));
        JsonObject flowObj = stateObj.getAsJsonObject(flow.getId());
        if (flowObj == null) throw new IllegalArgumentException("Could not load bundled packets for " + state.getId() + "/" + flow.getId() + "/" + ProtocolConstants.getVersionString(protocolId));
        for (String packetId : flowObj.keySet()) {
            Class<? extends Packet> packetClazz = mapMojangPacketId(packetId);
            if (packetClazz == null) {
                if (FishingBot.getInstance().getCurrentBot().getConfig().isLogPackets())
                    FishingBot.getLog().warning("Could not map packet id " + packetId + " in " + state.name() + " (" + flow.name() + ")");
                continue;
            }

            JsonObject packetData = flowObj.getAsJsonObject(packetId);
            int packetProtocolId = packetData.getAsJsonPrimitive("protocol_id").getAsInt();

            registerPacket(packetProtocolId, packetClazz);
        }
    }

    private JsonObject loadBundledPacketRegistry(int protocolId) {
        String file = getRegistryFileName(protocolId);
        if (file == null) return null;
        try {
            return parser.parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file))).getAsJsonObject();
        } catch (Throwable ex) {
            return null;
        }
    }

    private Class<? extends Packet> mapMojangPacketId(String mojangPacketId) {
        if (state == ProtocolState.HANDSHAKE && flow == ProtocolFlow.OUTGOING_PACKET) {
            if (mojangPacketId.equals("minecraft:intention"))
                return PacketOutHandshake.class;
        } else if (state == ProtocolState.LOGIN) {
            if (flow == ProtocolFlow.INCOMING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:login_disconnect": return PacketInLoginDisconnect.class;
                    case "minecraft:hello": return PacketInEncryptionRequest.class;
                    case "minecraft:login_finished":
                    case "minecraft:game_profile": return PacketInLoginSuccess.class;
                    case "minecraft:login_compression": return PacketInSetCompression.class;
                    case "minecraft:custom_query": return PacketInLoginPluginRequest.class;
                }
            } else if (flow == ProtocolFlow.OUTGOING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:hello": return PacketOutLoginStart.class;
                    case "minecraft:key": return PacketOutEncryptionResponse.class;
                    case "minecraft:custom_query_answer": return PacketOutLoginPluginResponse.class;
                    case "minecraft:login_acknowledged": return PacketOutLoginAcknowledge.class;
                }
            }
        } else if (state == ProtocolState.CONFIGURATION) {
            if (flow == ProtocolFlow.INCOMING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:finish_configuration": return PacketInFinishConfiguration.class;
                    case "minecraft:keep_alive": return PacketInKeepAlive.class;
                    case "minecraft:ping": return PacketInPing.class;
                    case "minecraft:resource_pack_push": return PacketInResourcePack.class;
                    case "minecraft:select_known_packs": return PacketInKnownPacks.class;
                    case "minecraft:registry_data": return PacketInRegistryData.class;
                }
            } else if (flow == ProtocolFlow.OUTGOING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:custom_payload": return PacketOutPluginMessage.class;
                    case "minecraft:finish_configuration": return PacketOutFinishConfiguration.class;
                    case "minecraft:keep_alive": return PacketOutKeepAlive.class;
                    case "minecraft:pong": return PacketOutPing.class;
                    case "minecraft:resource_pack": return PacketOutResourcePackResponse.class;
                    case "minecraft:select_known_packs": return PacketOutKnownPacks.class;
                }
            }
        } else if (state == ProtocolState.PLAY) {
            if (flow == ProtocolFlow.INCOMING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:add_entity": return PacketInSpawnEntity.class;
                    case "minecraft:change_difficulty": return PacketInDifficultySet.class;
                    case "minecraft:commands": return PacketInCommands.class;
                    case "minecraft:container_close": return PacketInWindowClose.class;
                    case "minecraft:container_set_content": return PacketInWindowItems.class;
                    case "minecraft:container_set_slot": return PacketInSetSlot.class;
                    case "minecraft:disconnect": return PacketInDisconnect.class;
                    case "minecraft:keep_alive": return PacketInKeepAlive.class;
                    case "minecraft:login": return PacketInJoinGame.class;
                    case "minecraft:move_entity_pos": return PacketInEntityPosition.class;
                    case "minecraft:move_entity_pos_rot": return PacketInEntityPositionRotation.class;
                    case "minecraft:open_screen": return PacketInOpenWindow.class;
                    case "minecraft:player_chat": return PacketInChatPlayer.class;
                    case "minecraft:player_info_remove": return PacketInPlayerListItemRemove.class;
                    case "minecraft:player_info_update": return PacketInPlayerListItem.class;
                    case "minecraft:player_position": return PacketInPlayerPosLook.class;
                    case "minecraft:player_rotation": return PacketInPlayerLook.class;
                    case "minecraft:resource_pack_push": return PacketInResourcePack.class;
                    case "minecraft:set_held_slot":
                    case "minecraft:set_carried_item": return PacketInHeldItemChange.class;
                    case "minecraft:set_entity_data": return PacketInEntityMetadata.class;
                    case "minecraft:set_entity_motion": return PacketInEntityVelocity.class;
                    case "minecraft:set_experience": return PacketInSetExperience.class;
                    case "minecraft:set_health": return PacketInUpdateHealth.class;
                    case "minecraft:start_configuration": return PacketInStartConfiguration.class;
                    case "minecraft:system_chat": return PacketInChatSystem.class;
                    case "minecraft:teleport_entity": return PacketInEntityTeleport.class;
                    case "minecraft:remove_entities": return PacketInDestroyEntities.class;
                    case "minecraft:confirm_transaction": return PacketInConfirmTransaction.class;
                    case "minecraft:play_compression": return PacketInSetCompressionLegacy.class;
                    case "minecraft:ping": return PacketInPing.class;
                    case "minecraft:set_player_inventory": return PacketInPlayerInventory.class;
                    case "minecraft:entity_position_sync": return PacketInEntityPositionSync.class;
                }
            } else if (flow == ProtocolFlow.OUTGOING_PACKET) {
                switch (mojangPacketId) {
                    case "minecraft:accept_teleportation": return PacketOutTeleportConfirm.class;
                    case "minecraft:chat_command": return PacketOutUnsignedChatCommand.class;
                    case "minecraft:chat_command_signed": return PacketOutChatCommand.class;
                    case "minecraft:chat": return PacketOutChatMessage.class;
                    case "minecraft:chat_session_update": return PacketOutChatSessionUpdate.class;
                    case "minecraft:client_command": return PacketOutClientStatus.class;
                    case "minecraft:client_information": return PacketOutClientSettings.class;
                    case "minecraft:configuration_acknowledged": return PacketOutAcknowledgeConfiguration.class;
                    case "minecraft:container_click": return PacketOutClickWindow.class;
                    case "minecraft:container_close": return PacketOutCloseInventory.class;
                    case "minecraft:custom_payload": return PacketOutPluginMessage.class;
                    case "minecraft:keep_alive": return PacketOutKeepAlive.class;
                    case "minecraft:move_player_pos": return PacketOutPosition.class;
                    case "minecraft:move_player_pos_rot": return PacketOutPosLook.class;
                    case "minecraft:player_command": return PacketOutEntityAction.class;
                    case "minecraft:resource_pack": return PacketOutResourcePackResponse.class;
                    case "minecraft:set_carried_item": return PacketOutHeldItemChange.class;
                    case "minecraft:use_item_on": return PacketOutBlockPlace.class;
                    case "minecraft:use_item": return PacketOutUseItem.class;
                    case "minecraft:swing": return PacketOutArmAnimation.class;
                    case "minecraft:confirm_transaction": return PacketOutConfirmTransaction.class;
                    case "minecraft:pong": return PacketOutPing.class;
                }
            }
        }
        return null;
    }

    public String getRegistryFileName(int protocolId) {
        if (protocolId == ProtocolConstants.AUTOMATIC)
            protocolId = ProtocolConstants.getLatest();
        //TODO: REMOVE SNAPSHOT DATA
        if (protocolId == ProtocolConstants.MC_1_21_2)
            return "mc_data/1_21_2/packets.json";
        String version = ProtocolConstants.getVersionString(protocolId);
        if (version.contains("/"))
            version = version.split("/")[0];
        if (version.contains("-"))
            version = version.split("-")[0];
        version = version.replace(".", "_").trim();
        return "mc_data/" + version + "/packets.json";
    }

    private void registerPacket(int id, Class<? extends Packet> clazz) {
        if (clazz == null) {
            FishingBot.getLog().severe("Tried to register null packet for: " + id);
            return;
        }
        if (registeredPackets.containsKey(id)) {
            FishingBot.getLog().severe("Tried to register packet twice: " + id + " is registered as " + registeredPackets.get(id).getSimpleName() + " wants to register as " + clazz.getSimpleName());
            return;
        }
        if (registeredPackets.containsValue(clazz)) {
            FishingBot.getLog().severe("Tried to register packet twice: " + id + " is registered as another packet with same class wants to register as " + clazz.getSimpleName());
            return;
        }
        registeredPackets.put(id, clazz);
    }

    public Class<? extends Packet> getPacket(int id) {
        return registeredPackets.get(id);
    }

    public int getId(Class<? extends Packet> clazz) throws InvalidPacketException {
        Integer id = registeredPackets.inverse().get(clazz);
        if (id == null) {
            FishingBot.getI18n().severe("network-unknown-packet-id", clazz.getSimpleName() + " (for " + state.name() +  ")", ProtocolConstants.getVersionString(FishingBot.getInstance().getCurrentBot().getServerProtocol()));
            FishingBot.getInstance().getCurrentBot().setRunning(false);
            FishingBot.getInstance().getCurrentBot().setWontConnect(true);
            throw new InvalidPacketException("Packet not registered: " + clazz.getSimpleName());
        }
        return id;
    }
}
