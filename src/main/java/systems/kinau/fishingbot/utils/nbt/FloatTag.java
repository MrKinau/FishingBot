package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class FloatTag extends Tag<Float> {

    @Override
    protected FloatTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readFloat());
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}
