package systems.kinau.fishingbot.utils;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

//TODO: Replace with any NBT API
public class NBTUtils {

    public static byte[] readNBT(ByteArrayDataInputWrapper in) {
        ByteArrayDataInputWrapper copy = in.clone();
        int startAvailable = in.getAvailable();
        int tabs = 0;
        boolean running = true;
        while (running) {
            byte type = in.readByte();
            if (startAvailable - in.getAvailable() == 1 && type != 10) {
                return new byte[]{type};
            }
            if (type == 0)
                tabs = readTag(type, (short)0, tabs, in);
            else {
                short nameLength = in.readShort();
                tabs = readTag(type, nameLength, tabs, in);
            }
            if(tabs == 0)
                running = false;
        }
        int stopAvailable = in.getAvailable();
        int read = startAvailable - stopAvailable;
        byte[] dataRead = new byte[read];
        copy.readBytes(dataRead);
        return dataRead;
    }

    private static int readTag(byte type, short nameLength, int tabs, ByteArrayDataInputWrapper in) {
        switch (type) {
            case 12: {
                /*System.out.println(addSpaces(tabs) + "TAG_Long_Array (" + */readName(nameLength, in)/* + ")")*/;
                tabs++;
                int size = in.readInt();
                for (int i = 0; i < size; i++) {
                    /*System.out.println(addSpaces(tabs) + "TAG_Long (" + i + "): " + */in.readLong()/*)*/;
                }
                tabs--;
                break;
            }
            case 11: {
                /*System.out.println(addSpaces(tabs) + "TAG_Int_Array (" + */readName(nameLength, in)/* + ")")*/;
                tabs++;
                int size = in.readInt();
                for (int i = 0; i < size; i++) {
                    /*System.out.println(addSpaces(tabs) + "TAG_Int (" + i + "): " + */in.readInt()/*)*/;
                }
                tabs--;
                break;
            }
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
                /*System.out.println(addSpaces(tabs) + "TAG_String (" + name + "): " + content);*/
                break;
            }
            case 7: {
                /*System.out.println(addSpaces(tabs) + "TAG_Byte_Array (" + */readName(nameLength, in)/* + ")")*/;
                tabs++;
                int size = in.readInt();
                for (int i = 0; i < size; i++) {
                    /*System.out.println(addSpaces(tabs) + "TAG_Byte (" + i + "): " + */in.readByte()/*)*/;
                }
                tabs--;
                break;
            }
            case 6: {
                /*System.out.println(addSpaces(tabs) + "TAG_Double (" + */readName(nameLength, in) /*+ "): " + */;in.readDouble()/*)*/;
                break;
            }
            case 5: {
                /*System.out.println(addSpaces(tabs) + "TAG_Float (" + */readName(nameLength, in) /*+ "): " + */;in.readFloat()/*)*/;
                break;
            }
            case 4: {
                /*System.out.println(addSpaces(tabs) + "TAG_Long (" + */readName(nameLength, in) /*+ "): " + */;in.readLong()/*)*/;
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
            case 1: {
                /*System.out.println(addSpaces(tabs) + "TAG_Byte (" + */readName(nameLength, in) /*+ "): " + */;in.readByte()/*)*/;
                break;
            }
            case 0: {
                tabs--;
                /*System.out.println(addSpaces(tabs) + "TAG_End");*/
                break;
            }
            default: {
                /*System.out.println("UNKNOWN: " + type);*/
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

// FOR DEBUG
//    private static String addSpaces(int tabs) {
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < tabs; i++)
//            sb.append("\t");
//        return sb.toString();
//    }
}