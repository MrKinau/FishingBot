package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class IntArrayTag extends Tag<int[]> {

    @Override
    protected IntArrayTag read(ByteArrayDataInputWrapper in) {
        int[] value = new int[in.readInt()];
        for (int i = 0; i < value.length; i++)
            value[i] = in.readInt();
        setValue(value);
        return this;
    }

    @Override
    public JsonElement toJson() {
        if (getValue() == null)
            return new JsonArray();
        JsonArray array = new JsonArray(getValue().length);
        for (int i : getValue()) {
            array.add(i);
        }
        return array;
    }
}
