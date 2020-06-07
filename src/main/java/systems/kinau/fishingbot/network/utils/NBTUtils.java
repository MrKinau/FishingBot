package systems.kinau.fishingbot.network.utils;

//TODO: Replace with any NBT API
public class NBTUtils {

    public static int readNBT(ByteArrayDataInputWrapper in) {
        int startAvailable = in.getAvailable();
        int tabs = 0;
        boolean running = true;
        while (running) {
            byte type = in.readByte();
            if(startAvailable - in.getAvailable() == 1 && type != 10)
                return 1;
            if(type == 0)
                tabs = readTag(type, (short)0, tabs, in);
            else {
                short nameLength = in.readShort();
                tabs = readTag(type, nameLength, tabs, in);
            }
            if(tabs == 0)
                running = false;
        }
        int stopAvailable = in.getAvailable();
        return startAvailable - stopAvailable;
    }

    private static int readTag(byte type, short nameLength, int tabs, ByteArrayDataInputWrapper in) {
        switch (type) {
            case 10: {
                readName(nameLength, in);
                tabs++;
                break;
            }
            case 9: {
                readName(nameLength, in);
                tabs++;
                byte listType = in.readByte();
                int listCount = in.readInt();
                for(int i = 0; i < listCount; i++)
                    tabs = readTag(listType, (short)0, tabs, in);
                tabs--;
                break;
            }
            case 8: {
                readName(nameLength, in);
                int stringLength = in.readUnsignedShort();
                byte[] stringData = new byte[stringLength];
                in.readBytes(stringData);
                break;
            }
            case 6: {
                readName(nameLength, in);
                in.readDouble();
                break;
            }
            case 5: {
                readName(nameLength, in);
                in.readFloat();
                break;
            }
            case 4: {
                readName(nameLength, in);
                in.readLong();
                break;
            }
            case 3: {
                readName(nameLength, in);
                in.readInt();
                break;
            }
            case 2: {
                readName(nameLength, in);
                in.readShort();
                break;
            }
            case 1: {
                readName(nameLength, in);
                in.readByte();
                break;
            }
            case 0: {
                tabs--;
                break;
            }
        }
        return tabs;
    }

    private static String readName(short length, ByteArrayDataInputWrapper in) {
        if(length == 0)
            return "";
        byte[] name = new byte[length];
        in.readBytes(name);
        return new String(name);
    }

    private static String addSpaces(int tabs) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < tabs; i++)
            sb.append("\t");
        return sb.toString();
    }
}