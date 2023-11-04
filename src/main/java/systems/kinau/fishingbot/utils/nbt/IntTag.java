package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class IntTag extends Tag<Integer> {

    @Override
    protected IntTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readInt());
        return this;
    }
}
