package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ShortTag extends Tag<Short> {

    @Override
    protected ShortTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readShort());
        return this;
    }
}
