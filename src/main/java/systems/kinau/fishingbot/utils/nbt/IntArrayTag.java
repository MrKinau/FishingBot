package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class IntArrayTag extends Tag<int[]> {

    @Override
    protected IntArrayTag read(ByteArrayDataInputWrapper in) {
        int[] value = new int[in.readInt()];
        for (int i = 0; i < value.length; i++)
            value[i] = in.readInt();
        setValue(value);
        return this;
    }
}
