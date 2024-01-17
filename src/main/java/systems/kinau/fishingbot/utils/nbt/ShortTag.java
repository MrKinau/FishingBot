package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ShortTag extends Tag<Short> {

    @Override
    protected ShortTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readShort());
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}
