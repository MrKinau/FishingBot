package systems.kinau.fishingbot.network.utils;

import org.apache.commons.codec.Charsets;

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
                /*System.out.println(addSpaces(tabs) + "TAG_Compound (" + */readName(nameLength, in) /*+ ")")*/;
                tabs++;
                break;
            }
            case 9: {
                /*System.out.println(addSpaces(tabs) + "TAG_List (" + */readName(nameLength, in) /*+ ")")*/;
                tabs++;
                byte listType = in.readByte();
                int listCount = in.readInt();
                for(int i = 0; i < listCount; i++)
                    tabs = readTag(listType, (short)0, tabs, in);
                tabs--;
                break;
            }
            case 8: {
                String name = readName(nameLength, in);
                int stringLength = in.readUnsignedShort();
                byte[] stringData = new byte[stringLength];
                in.readBytes(stringData);
                String content = new String(stringData, Charsets.UTF_8);
                /*System.out.println(addSpaces(tabs) + "TAG_String (" + name + "): " + content);*/
                break;
            }
            case 3: {
                /*System.out.println(addSpaces(tabs) + "TAG_Int (" + */readName(nameLength, in) /*+ "): " + */;in.readInt()/*)*/;
                break;
            }
            case 2: {
                /*System.out.println(addSpaces(tabs) + "TAG_Short (" + */readName(nameLength, in) /*+ "): " + */;in.readShort()/*)*/;
                break;
            }
            case 0: {
                tabs--;
                /*System.out.println(addSpaces(tabs) + "TAG_End");*/
                break;
            }
            default: {
                System.out.println("UNKNOWN: " + type);
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
