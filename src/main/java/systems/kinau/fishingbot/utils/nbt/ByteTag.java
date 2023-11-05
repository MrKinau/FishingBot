package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ByteTag extends Tag<Byte> {

    @Override
    protected ByteTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readByte());
        return this;
    }
}
