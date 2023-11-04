package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class LongTag extends Tag<Long> {

    @Override
    protected LongTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readLong());
        return this;
    }
}
