/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.google.common.base.Charsets;
import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.InvalidPacketException;
import systems.kinau.fishingbot.network.utils.OverflowPacketException;
import systems.kinau.fishingbot.utils.NBTUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public abstract class Packet {

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

    protected static String readString(ByteArrayDataInputWrapper buf) {
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

    protected UUID readUUID(ByteArrayDataInputWrapper input) {
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

    public static void writeSlot(Slot slot, ByteArrayDataOutput output) {
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_13_2) {
            output.writeBoolean(slot.isPresent());
            if (slot.isPresent()) {
                writeVarInt(slot.getItemId(), output);
                output.writeByte(slot.getItemCount());
                output.write(slot.getNbtData());
            }
        } else if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_13) {
            if (!slot.isPresent()) {
                output.writeShort(-1);
                return;
            }
            output.writeShort(slot.getItemId());
            output.writeByte(slot.getItemCount());
            output.write(slot.getNbtData());
        } else {
            if (!slot.isPresent()) {
                output.writeShort(-1);
                return;
            }
            output.writeShort(slot.getItemId());
            output.writeByte(slot.getItemCount());
            output.writeShort(slot.getItemDamage());
            output.write(slot.getNbtData());
        }
    }

    public static Slot readSlot(ByteArrayDataInputWrapper input) {
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_13_2) {
            boolean present = input.readBoolean();
            if (present) {
                int itemId = readVarInt(input);
                byte itemCount = input.readByte();
                byte[] nbtData = NBTUtils.readNBT(input);
                int damage = -1;
                try {
                    NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(nbtData.clone()), false);
                    Tag tag = nbtStream.readTag();
                    if (tag.getType() == TagType.TAG_COMPOUND) {
                        CompoundMap tagMap = ((CompoundTag)tag).getValue();
                        if (tagMap.containsKey("Damage"))
                            damage = ((IntTag)tagMap.get("Damage")).getValue();
                    }
                } catch (IOException e) {
                }
                return new Slot(true, itemId, itemCount, (short) damage, nbtData);
            } else
                return Slot.EMPTY;
        } else if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_13) {
            int itemId = input.readShort();
            if (itemId == -1)
                return Slot.EMPTY;
            byte itemCount = input.readByte();
            byte[] nbtData = NBTUtils.readNBT(input);
            int damage = -1;
            try {
                NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(nbtData.clone()), false);
                Tag tag = nbtStream.readTag();
                if (tag.getType() == TagType.TAG_COMPOUND) {
                    CompoundMap tagMap = ((CompoundTag)tag).getValue();
                    if (tagMap.containsKey("Damage"))
                        damage = ((IntTag)tagMap.get("Damage")).getValue();
                }
            } catch (IOException e) {
            }
            return new Slot(true, itemId, itemCount, (short) damage, nbtData);
        } else {
            int itemId = input.readShort();
            if (itemId == -1)
                return Slot.EMPTY;
            byte itemCount = input.readByte();
            short itemDamage = input.readShort();
            byte[] nbtData = NBTUtils.readNBT(input);
            return new Slot(true, itemId, itemCount, itemDamage, nbtData);
        }
    }

}
