package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class DoubleTag extends Tag<Double> {

    @Override
    protected DoubleTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readDouble());
        return this;
    }
}
