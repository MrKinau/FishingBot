package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class DoubleTag extends Tag<Double> {

    @Override
    protected DoubleTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readDouble());
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}
