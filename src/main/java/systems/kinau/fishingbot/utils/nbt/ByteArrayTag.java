package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class ByteArrayTag extends Tag<byte[]> {

    @Override
    protected ByteArrayTag read(ByteArrayDataInputWrapper in) {
        byte[] value = new byte[in.readInt()];
        in.readBytes(value);
        setValue(value);
        return this;
    }

    @Override
    public JsonElement toJson() {
        if (getValue() == null)
            return new JsonArray();
        JsonArray array = new JsonArray(getValue().length);
        for (byte b : getValue()) {
            array.add(b);
        }
        return array;
    }
}
