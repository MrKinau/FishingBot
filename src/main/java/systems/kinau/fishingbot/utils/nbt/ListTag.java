package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListTag extends Tag<List<? extends Tag<?>>> {

    @Override
    protected ListTag read(ByteArrayDataInputWrapper in) {
        byte listTypeId = in.readByte();
        int listCount = in.readInt();
        List<? extends Tag<?>> value = new ArrayList<>(listCount);
        for (int i = 0; i < listCount; i++)
            value.add(readNextTag(in, listTypeId));
        setValue(value);
        return this;
    }

    @Override
    public String toString(int tabs) {
        StringBuilder sb = new StringBuilder(addTabs(tabs) + getClass().getSimpleName() + " (" + Optional.ofNullable(getName()).orElse("") + "): ");
        for (Tag<?> tag : getValue())
            sb.append("\n").append(tag.toString(tabs + 1));
        return sb.toString();
    }

    @Override
    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        if (getValue() == null) return array;
        getValue().forEach(tag -> array.add(tag.toJson()));
        return array;
    }
}
