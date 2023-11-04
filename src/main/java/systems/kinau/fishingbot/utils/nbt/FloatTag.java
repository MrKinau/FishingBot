package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class FloatTag extends Tag<Float> {

    @Override
    protected FloatTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readFloat());
        return this;
    }
}
