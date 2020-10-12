/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

//Credits for this go to Lukasz Baran who made this wrapper class; can be found here http://stackoverflow.com/questions/9378862/alternative-to-com-google-common-io-ByteArrayDataInputWrapper
public class ByteArrayDataInputWrapper implements ByteArrayDataInput {

    private static final int INT_LENGTH = 4;
    private static final int FLOAT_LENGTH = 4;
    private static final int LONG_LENGTH = 8;
    private static final int DOUBLE_LENGTH = 8;

    private ByteArrayDataInput in;
    private int available;

    public ByteArrayDataInputWrapper(byte[] binary) {
        if (binary != null) {
            in = ByteStreams.newDataInput(binary);
            available = binary.length;
        }
    }

    private ByteArrayDataInputWrapper(ByteArrayDataInput in, int available) {
        this.in = in;
        this.available = available;
    }

    public int getAvailable() {
        return available;
    }

    public ByteArrayDataInputWrapper clone() {
        byte[] fully = new byte[available];
        readFully(fully);
        this.in = ByteStreams.newDataInput(fully.clone());
        this.available = fully.length;
        return new ByteArrayDataInputWrapper(ByteStreams.newDataInput(fully.clone()), available);
    }

    @Override
    public void readFully(byte[] b) {
        in.readFully(b);
        available -= b.length;
    }

    @Override
    public void readFully(byte[] b, int off, int len) {
        in.readFully(b, off, len);
        available -= len;
    }

    @Override
    public int skipBytes(int n) {
        int skipped = in.skipBytes(n);
        available -= skipped;
        return skipped;
    }

    @Override
    public boolean readBoolean() {
        boolean result = in.readBoolean();
        available--;
        return result;
    }

    @Override
    public byte readByte() {
        byte result = in.readByte();
        available--;
        return result;
    }

    @Override
    public int readUnsignedByte() {
        int result = in.readUnsignedByte();
        available--;
        return result;
    }

    @Override
    public short readShort() {
        short result = in.readShort();
        available -= 2;
        return result;
    }

    @Override
    public int readUnsignedShort() {
        int result = in.readUnsignedShort();
        available -= 2;
        return result;
    }

    @Override
    public char readChar() {
        char result = in.readChar();
        available -= 2;
        return result;
    }

    @Override
    public int readInt() {
        int result = in.readInt();
        available -= INT_LENGTH;
        return result;
    }

    @Override
    public long readLong() {
        long result = in.readLong();
        available -= LONG_LENGTH;
        return result;
    }

    @Override
    public float readFloat() {
        float result = in.readFloat();
        available -= FLOAT_LENGTH;
        return result;
    }

    @Override
    public double readDouble() {
        double result = in.readDouble();
        available -= DOUBLE_LENGTH;
        return result;
    }

    @Override
    public String readLine() {
        String result = in.readLine();
        if (result != null) {
            available -= result.length();
        }
        return result;
    }

    @Override
    public String readUTF() {
        String result = in.readUTF();
        if (result != null) {
            available -= result.length();
        }
        return result;
    }

    public void readBytes(byte[] b) {
        readFully(b, 0, b.length);
    }
}