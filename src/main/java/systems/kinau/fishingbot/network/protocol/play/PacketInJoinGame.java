/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.JoinGameEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
public class PacketInJoinGame extends Packet {

    private int eid;
    private int gamemode;
    private String[] worldIdentifier;
    private String dimension;
    private String spawnWorld;
    private long hashedSeed;
    private int difficulty;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private String levelType;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean debug;
    private boolean flat;
    private boolean hardcore;
    private int portalCooldown;
    private int seaLevel;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        // only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (protocolId <= ProtocolConstants.MC_1_9) {
            this.eid = in.readInt();                         // entity ID
            this.gamemode = in.readUnsignedByte();           // gamemode
            this.dimension = String.valueOf(in.readByte());  // dimension
            this.difficulty = in.readUnsignedByte();         // difficulty
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.levelType = readString(in);                 // level type
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
        } else if (protocolId <= ProtocolConstants.MC_1_13_2) {
            this.eid = in.readInt();                         // entity ID
            this.gamemode = in.readUnsignedByte();           // gamemode
            this.dimension = String.valueOf(in.readInt());   // dimension
            this.difficulty = in.readUnsignedByte();         // difficulty
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.levelType = readString(in);                 // level type
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
        } else if (protocolId <= ProtocolConstants.MC_1_14_4) {
            this.eid = in.readInt();                         // entity ID
            this.gamemode = in.readUnsignedByte();           // gamemode
            this.dimension = String.valueOf(in.readInt());   // dimension
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.levelType = readString(in);                 // level type
            this.viewDistance = readVarInt(in);              // view distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
        } else if (protocolId <= ProtocolConstants.MC_1_15_2) {
            this.eid = in.readInt();                         // entity ID
            this.gamemode = in.readUnsignedByte();           // gamemode
            this.dimension = String.valueOf(in.readInt());   // dimension
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.levelType = readString(in);                 // level type
            this.viewDistance = readVarInt(in);              // view distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
        } else if (protocolId <= ProtocolConstants.MC_1_16_1) {
            this.eid = in.readInt();                         // entity ID
            this.gamemode = in.readUnsignedByte();           // gamemode
            in.readUnsignedByte();                           // previous gamemode
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            readNBT(in, protocolId);                         // dimension codec (dont use, just skip it)
            this.dimension = readString(in);                 // dimension
            this.spawnWorld = readString(in);                // spawn world name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
        } else if (protocolId <= ProtocolConstants.MC_1_17_1) {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            readNBT(in, protocolId);                         // dimension codec (dont use, just skip it)
            readNBT(in, protocolId);                         // spawn dimension
            this.spawnWorld = readString(in);                // spawn world name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
        } else if (protocolId <= ProtocolConstants.MC_1_18_2) {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            readNBT(in, protocolId);                         // dimension codec (dont use, just skip it)
            readNBT(in, protocolId);                         // spawn dimension
            this.spawnWorld = readString(in);                // spawn world name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.maxPlayers = in.readUnsignedByte();         // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.simulationDistance = readVarInt(in);        // simulation distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
        } else if (protocolId <= ProtocolConstants.MC_1_19_4) {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            readNBT(in, protocolId);                         // registry codec
            readString(in);                                  // dimension type
            this.spawnWorld = readString(in);                // dimension name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.maxPlayers = readVarInt(in);                // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.simulationDistance = readVarInt(in);        // simulation distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
            if (in.readBoolean()) {                          // has last death location
                readString(in);                              // last death dimension
                in.readLong();                               // last death position
            }
        } else if (protocolId <= ProtocolConstants.MC_1_20) {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            readNBT(in, protocolId);                         // registry codec
            readString(in);                                  // dimension type
            this.spawnWorld = readString(in);                // dimension name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.maxPlayers = readVarInt(in);                // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.simulationDistance = readVarInt(in);        // simulation distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
            if (in.readBoolean()) {                          // has last death location
                readString(in);                              // last death dimension
                in.readLong();                               // last death position
            }
            this.portalCooldown = readVarInt(in);
        } else if (protocolId <= ProtocolConstants.MC_1_20_3) {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            this.maxPlayers = readVarInt(in);                // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.simulationDistance = readVarInt(in);        // simulation distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            in.readBoolean();                                // doLimitedCrafting
            readString(in);                                  // dimension type
            this.spawnWorld = readString(in);                // dimension name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
            if (in.readBoolean()) {                          // has last death location
                readString(in);                              // last death dimension
                in.readLong();                               // last death position
            }
            this.portalCooldown = readVarInt(in);
        } else if (protocolId <= ProtocolConstants.MC_1_21) {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            this.maxPlayers = readVarInt(in);                // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.simulationDistance = readVarInt(in);        // simulation distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            in.readBoolean();                                // doLimitedCrafting
            readVarInt(in);                                  // dimension type
            this.spawnWorld = readString(in);                // dimension name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
            if (in.readBoolean()) {                          // has last death location
                readString(in);                              // last death dimension
                in.readLong();                               // last death position
            }
            this.portalCooldown = readVarInt(in);
        } else {
            this.eid = in.readInt();                         // entity ID
            this.hardcore = in.readBoolean();                // is hardcore
            int worldCount = readVarInt(in);                 // count of worlds
            this.worldIdentifier = new String[worldCount];   // identifier for all worlds
            for (int i = 0; i < worldCount; i++)
                this.worldIdentifier[i] = readString(in);
            this.maxPlayers = readVarInt(in);                // maxPlayer
            this.viewDistance = readVarInt(in);              // view distance
            this.simulationDistance = readVarInt(in);        // simulation distance
            this.reducedDebugInfo = in.readBoolean();        // reduced Debug info
            this.enableRespawnScreen = in.readBoolean();     // set to false when the doImmediateRespawn gamerule is true
            in.readBoolean();                                // doLimitedCrafting
            readVarInt(in);                                  // dimension type
            this.spawnWorld = readString(in);                // dimension name
            this.hashedSeed = in.readLong();                 // first 8 bytes of the SHA-256 hash of the world's seed
            this.gamemode = in.readUnsignedByte();           // current gamemode
            in.readUnsignedByte();                           // previous gamemode
            this.debug = in.readBoolean();                   // debug world
            this.flat = in.readBoolean();                    // flat world
            if (in.readBoolean()) {                          // has last death location
                readString(in);                              // last death dimension
                in.readLong();                               // last death position
            }
            this.portalCooldown = readVarInt(in);
            this.seaLevel = readVarInt(in);
        }

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(
                new JoinGameEvent(eid, gamemode, worldIdentifier, dimension, spawnWorld,
                        hashedSeed, difficulty, maxPlayers, viewDistance, simulationDistance, levelType,
                        reducedDebugInfo, enableRespawnScreen, debug, flat));
    }
}
