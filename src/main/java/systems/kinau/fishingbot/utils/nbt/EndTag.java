package systems.kinau.fishingbot.utils.nbt;

import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class EndTag extends Tag<Void> {

    @Override
    protected EndTag read(ByteArrayDataInputWrapper in) {
        return this;
    }
}
