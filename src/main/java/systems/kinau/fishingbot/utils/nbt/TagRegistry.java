package systems.kinau.fishingbot.utils.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TagRegistry {

    private static final Map<Byte, Supplier<? extends Tag<?>>> registeredTags = new HashMap<>();
    private static final Map<Class<? extends Tag>, Byte> classToId = new HashMap<>();

    public static byte TAG_END_ID = 0;
    public static byte TAG_BYTE_ID = 1;
    public static byte TAG_SHORT_ID = 2;
    public static byte TAG_INT_ID = 3;
    public static byte TAG_LONG_ID = 4;
    public static byte TAG_FLOAT_ID = 5;
    public static byte TAG_DOUBLE_ID = 6;
    public static byte TAG_BYTE_ARRAY_ID = 7;
    public static byte TAG_STRING_ID = 8;
    public static byte TAG_LIST_ID = 9;
    public static byte TAG_COMPOUND_ID = 10;
    public static byte TAG_INT_ARRAY_ID = 11;
    public static byte TAG_LONG_ARRAY_ID = 12;

    static {
        registeredTags.put(TAG_END_ID, EndTag::new);
        registeredTags.put(TAG_BYTE_ID, ByteTag::new);
        registeredTags.put(TAG_SHORT_ID, ShortTag::new);
        registeredTags.put(TAG_INT_ID, IntTag::new);
        registeredTags.put(TAG_LONG_ID, LongTag::new);
        registeredTags.put(TAG_FLOAT_ID, FloatTag::new);
        registeredTags.put(TAG_DOUBLE_ID, DoubleTag::new);
        registeredTags.put(TAG_BYTE_ARRAY_ID, ByteArrayTag::new);
        registeredTags.put(TAG_STRING_ID, StringTag::new);
        registeredTags.put(TAG_LIST_ID, ListTag::new);
        registeredTags.put(TAG_COMPOUND_ID, CompoundTag::new);
        registeredTags.put(TAG_INT_ARRAY_ID, IntArrayTag::new);
        registeredTags.put(TAG_LONG_ARRAY_ID, LongArrayTag::new);

        classToId.put(EndTag.class, TAG_END_ID);
        classToId.put(ByteTag.class, TAG_BYTE_ID);
        classToId.put(ShortTag.class, TAG_SHORT_ID);
        classToId.put(IntTag.class, TAG_INT_ID);
        classToId.put(LongTag.class, TAG_LONG_ID);
        classToId.put(FloatTag.class, TAG_FLOAT_ID);
        classToId.put(DoubleTag.class, TAG_DOUBLE_ID);
        classToId.put(ByteArrayTag.class, TAG_BYTE_ARRAY_ID);
        classToId.put(StringTag.class, TAG_STRING_ID);
        classToId.put(ListTag.class, TAG_LIST_ID);
        classToId.put(CompoundTag.class, TAG_COMPOUND_ID);
        classToId.put(IntArrayTag.class, TAG_INT_ARRAY_ID);
        classToId.put(LongArrayTag.class, TAG_LONG_ARRAY_ID);
    }

    public static <T extends Tag<?>> T createTag(Class<T> clazz) {
        Byte id = getTagId(clazz);
        if (id == null) return null;
        return (T) registeredTags.get(id).get();
    }

    public static <T extends Tag<?>> T createTag(byte id) {
        return (T) registeredTags.get(id).get();
    }

    public static Byte getTagId(Class<? extends Tag<?>> clazz) {
        return classToId.get(clazz);
    }
}
