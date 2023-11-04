package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class LongArrayTag extends Tag<long[]> {

    @Override
    protected LongArrayTag read(ByteArrayDataInputWrapper in) {
        long[] value = new long[in.readInt()];
        for (int i = 0; i < value.length; i++)
            value[i] = in.readLong();
        setValue(value);
        return this;
    }
}
