/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class PacketHelper {

    public static String readString(ByteArrayDataInputWrapper in) { //read a string from a bytebuffer
        int length;
        String s = "";
        try {
            length = readVarInt(in);
            if (length < 0) {
                throw new IOException(
                        "Received string length is less than zero! Weird string!");
            }

            if (length == 0) {
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

    public static String readString(DataInputStream in) {
        int length;
        String s = "";
        try {
            length = readVarInt(in);
            if (length < 0)
                throw new IOException("Received string length is less than zero! Weird string!");

            if (length == 0)
                return "";

            byte[] b = new byte[length];
            in.readFully(b, 0, length);
            s = new String(b, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.read();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return i;
    }

    public static int readVarInt(ByteArrayDataInput ins) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = ins.readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return i;
    }

    public static int[] readVarIntt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        int b = 0;
        while (true) {
            int k = in.read();
            b += 1;
            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        int[] result = {i, b};
        return result;
    }

    public static void writeString(ByteArrayDataOutput out, String s) {
        writeVarInt(out, s.length());
        try {
            out.write(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void writeVarInt(ByteArrayDataOutput outs, int paramInt) {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                outs.writeByte((byte) paramInt);
                return;
            }

            outs.writeByte((byte) (paramInt & 0x7F | 0x80));
            paramInt >>>= 7;
        }
    }

    public static UUID readUUID(ByteArrayDataInputWrapper ins){ //reads a UUID field
        return new UUID(ins.readLong(), ins.readLong());
    }

    public static byte[] readBytesFromStreamV(ByteArrayDataInputWrapper par0DataInputStream) throws IOException {
        int var1 = readVarInt(par0DataInputStream);
        if (var1 < 0) {
            throw new IOException("Key was smaller than nothing!  Weird key!");
        } else {
            byte[] var2 = new byte[var1];
            par0DataInputStream.readFully(var2);
            return var2;
        }
    }

}
