package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class IntTag extends Tag<Integer> {

    @Override
    protected IntTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readInt());
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}
