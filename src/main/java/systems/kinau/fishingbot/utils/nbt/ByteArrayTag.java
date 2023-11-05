package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ByteArrayTag extends Tag<byte[]> {

    @Override
    protected ByteArrayTag read(ByteArrayDataInputWrapper in) {
        byte[] value = new byte[in.readInt()];
        in.readBytes(value);
        setValue(value);
        return this;
    }
}
