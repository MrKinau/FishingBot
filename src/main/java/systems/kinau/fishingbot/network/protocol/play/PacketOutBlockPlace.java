package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.bot.MovingObjectPositionBlock;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.LocationUtils;

//TODO: This actually is the UseItemPacket in newer versions
@AllArgsConstructor
public class PacketOutBlockPlace extends Packet {

    @Getter private Hand hand;
    @Getter private int x;
    @Getter private int y;
    @Getter private int z;
    @Getter private BlockFace blockFace;
    @Getter private float cursorX;
    @Getter private float cursorY;
    @Getter private float cursorZ;
    @Getter private boolean insideBlock;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId >= ProtocolConstants.MINECRAFT_1_14) {
            writeVarInt(hand.ordinal(), out);
            MovingObjectPositionBlock movObjPos = new MovingObjectPositionBlock(LocationUtils.toBlockPos(x, y, z), blockFace, cursorX, cursorY, cursorZ, insideBlock);
            writeMovingObjectPosition(movObjPos, out);
            if (protocolId >= ProtocolConstants.MINECRAFT_1_19)
                writeVarInt(0, out); // sequence
        } else if (protocolId >= ProtocolConstants.MINECRAFT_1_9) {
            out.writeLong(LocationUtils.toBlockPos(x, y, z));
            writeVarInt(blockFace == PacketOutBlockPlace.BlockFace.UNSET ? 255 : blockFace.ordinal(), out);
            writeVarInt(hand.ordinal(), out);
            if (protocolId >= ProtocolConstants.MINECRAFT_1_11) {
                out.writeFloat(cursorX);
                out.writeFloat(cursorY);
                out.writeFloat(cursorZ);
            } else {
                out.writeByte((int) cursorX);
                out.writeByte((int) cursorY);
                out.writeByte((int) cursorZ);
            }
        } else {
            System.err.println("Use PacketOutUseItem for 1.8");
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // Only outgoing packet
    }

    public enum Hand {
        MAIN_HAND,
        OFF_HAND
    }

    public enum BlockFace {
        BOTTOM,
        TOP,
        NORTH,
        SOUTH,
        WEST,
        EAST,
        UNSET;

        public static BlockFace byOrdinal(int ordinal) {
            if (ordinal == 255)
                return UNSET;
            return BlockFace.values()[ordinal];
        }
    }

}
