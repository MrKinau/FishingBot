/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import com.google.common.base.Charsets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.bot.MovingObjectPositionBlock;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.protocol.play.PacketOutBlockPlace;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.InvalidPacketException;
import systems.kinau.fishingbot.network.utils.OverflowPacketException;
import systems.kinau.fishingbot.utils.ChatComponentUtils;
import systems.kinau.fishingbot.utils.nbt.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public abstract class Packet {

    private static final JsonParser PARSER = new JsonParser();

    public abstract void write(ByteArrayDataOutput out, int protocolId) throws IOException;

    public abstract void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException;

    public static void writeString(String s, ByteArrayDataOutput buf) {
        if (s.length() > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length()));
        }

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.write(b);
    }

    public static String readString(ByteArrayDataInputWrapper buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    public static int readVarInt(ByteArrayDataInputWrapper input) {
        return readVarInt(input, 5);
    }

    private static int readVarInt(ByteArrayDataInputWrapper input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new InvalidPacketException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static void writeVarInt(int value, ByteArrayDataOutput output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeUUID(UUID uuid, ByteArrayDataOutput output) {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteArrayDataInputWrapper input) {
        return new UUID(input.readLong(), input.readLong());
    }

    public byte[] readBytesFromStream(ByteArrayDataInputWrapper par0DataInputStream) {
        int var1 = readVarInt(par0DataInputStream);
        if (var1 < 0) {
            throw new OverflowPacketException("Key was smaller than nothing! Weird key!");
        } else {
            byte[] var2 = new byte[var1];
            par0DataInputStream.readFully(var2);
            return var2;
        }
    }

    public static int readVarInt(DataInputStream in) throws IOException { //reads a varint from the stream
        int i = 0;
        int j = 0;
        while (true){
            int k = in.read();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new InvalidPacketException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return i;
    }

    public static int[] readVarIntt(DataInputStream in) throws IOException { //reads a varint from the stream, returning both the length and the value
        int i = 0;
        int j = 0;
        int b = 0;
        while (true){
            int k = in.read();
            b += 1;
            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new InvalidPacketException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        int[] result = {i,b};
        return result;
    }

    public static void writeVarLong(long value, ByteArrayDataOutput output) {
        while ((value & -128L) != 0L) {
            output.writeByte((int) (value & 127L) | 128);
            value >>>= 7;
        }

        output.writeByte((int) value);
    }

    public static long readVarLong(ByteArrayDataInput input) {
        long i = 0L;
        int j = 0;

        byte b0;

        do {
            b0 = input.readByte();
            i |= (long) (b0 & 127) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public static String readString(DataInputStream in) {
        int length;
        String s = "";
        try {
            length = readVarInt(in);
            if (length < 0) {
                throw new IOException(
                        "Received string length is less than zero! Weird string!");
            }

            if(length == 0){
                return "";
            }
            byte[] b = new byte[length];
            in.readFully(b, 0, length);
            s = new String(b, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static NBTTag readNBT(ByteArrayDataInputWrapper input, int protocolId) {
        return new NBTTag(input, protocolId);
    }

    public static void writeNBT(NBTTag tag, ByteArrayDataOutput output) {
        output.write(tag.getData());
    }

    public static String readChatComponent(ByteArrayDataInputWrapper input, int protocolId) {
        JsonObject chatComponent = null;
        try {
            if (protocolId < ProtocolConstants.MINECRAFT_1_20_3) {
                String text = readString(input);
                chatComponent = PARSER.parse(text).getAsJsonObject();
            } else {
                NBTTag nbt = readNBT(input, protocolId);
                if (nbt.getTag() instanceof StringTag)
                    return ((StringTag) nbt.getTag()).getValue();
                chatComponent = nbt.getTag().toJson().getAsJsonObject();
            }
        } catch (Exception ignored) {
            // Ignored
        }
        return ChatComponentUtils.toPlainText(chatComponent);
    }

    public static void writeSlot(Slot slot, ByteArrayDataOutput output, int protocolId) {
        if (protocolId >= ProtocolConstants.MINECRAFT_1_13_2) {
            output.writeBoolean(slot.isPresent());
            if (slot.isPresent()) {
                writeVarInt(slot.getItemId(), output);
                output.writeByte(slot.getItemCount());
                writeNBT(slot.getNbtData(), output);
            }
        } else if (protocolId >= ProtocolConstants.MINECRAFT_1_13) {
            if (!slot.isPresent()) {
                output.writeShort(-1);
                return;
            }
            output.writeShort(slot.getItemId());
            output.writeByte(slot.getItemCount());
            writeNBT(slot.getNbtData(), output);
        } else {
            if (!slot.isPresent()) {
                output.writeShort(-1);
                return;
            }
            output.writeShort(slot.getItemId());
            output.writeByte(slot.getItemCount());
            output.writeShort(slot.getItemDamage());
            writeNBT(slot.getNbtData(), output);
        }
    }

    public static Slot readSlot(ByteArrayDataInputWrapper input, int protocolId) {
        if (protocolId >= ProtocolConstants.MINECRAFT_1_13_2) {
            boolean present = input.readBoolean();
            if (present) {
                int itemId = readVarInt(input);
                byte itemCount = input.readByte();
                NBTTag tag = readNBT(input, protocolId);
                int damage = -1;
                if (tag.getTag() instanceof CompoundTag) {
                    damage = Optional.ofNullable(((CompoundTag)tag.getTag()).get("Damage", IntTag.class))
                            .map(Tag::getValue)
                            .orElse(-1);
                }
                return new Slot(true, itemId, itemCount, damage, tag);
            } else
                return Slot.EMPTY;
        } else if (protocolId >= ProtocolConstants.MINECRAFT_1_13) {
            int itemId = input.readShort();
            if (itemId == -1)
                return Slot.EMPTY;
            byte itemCount = input.readByte();
            NBTTag tag = readNBT(input, protocolId);
            int damage = -1;
            if (tag.getTag() instanceof CompoundTag) {
                damage = Optional.ofNullable(((CompoundTag)tag.getTag()).get("Damage", IntTag.class))
                        .map(Tag::getValue)
                        .orElse(-1);
            }
            return new Slot(true, itemId, itemCount, damage, tag);
        } else {
            int itemId = input.readShort();
            if (itemId == -1)
                return Slot.EMPTY;
            byte itemCount = input.readByte();
            short itemDamage = input.readShort();
            NBTTag tag = readNBT(input, protocolId);
            return new Slot(true, itemId, itemCount, itemDamage, tag);
        }
    }

    public static MovingObjectPositionBlock readMovingObjectPosition(ByteArrayDataInputWrapper input) {
        long blockPos = input.readLong();
        PacketOutBlockPlace.BlockFace blockFace = PacketOutBlockPlace.BlockFace.byOrdinal(readVarInt(input));
        float dx = input.readFloat();
        float dy = input.readFloat();
        float dz = input.readFloat();
        boolean flag = input.readBoolean();
        return new MovingObjectPositionBlock(blockPos, blockFace, dx, dy, dz, flag);
    }

    public static void writeMovingObjectPosition(MovingObjectPositionBlock movingObjectPositionBlock, ByteArrayDataOutput output) {
        output.writeLong(movingObjectPositionBlock.getBlockPos());
        PacketOutBlockPlace.BlockFace blockFace = movingObjectPositionBlock.getDirection();
        writeVarInt(blockFace == PacketOutBlockPlace.BlockFace.UNSET ? 255 : blockFace.ordinal(), output);
        output.writeFloat(movingObjectPositionBlock.getDx());
        output.writeFloat(movingObjectPositionBlock.getDy());
        output.writeFloat(movingObjectPositionBlock.getDz());
        output.writeBoolean(movingObjectPositionBlock.isFlag());
    }

    public static <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> type, ByteArrayDataInput input) {
        E[] ae = type.getEnumConstants();
        BitSet bitset = readFixedBitSet(ae.length, input);
        EnumSet<E> enumset = EnumSet.noneOf(type);

        for (int i = 0; i < ae.length; ++i) {
            if (bitset.get(i)) {
                enumset.add(ae[i]);
            }
        }

        return enumset;
    }

    public static BitSet readFixedBitSet(int size, ByteArrayDataInput input) {
        byte[] abyte = new byte[-Math.floorDiv(-size, 8)];

        input.readFully(abyte);
        return BitSet.valueOf(abyte);
    }

    public static void writeFixedBitSet(BitSet bitSet, int size, ByteArrayDataOutput output) {
        if (bitSet.length() <= size) {
            byte[] abyte = bitSet.toByteArray();

            output.write(Arrays.copyOf(abyte, -Math.floorDiv(-size, 8)));
        }
    }

}
