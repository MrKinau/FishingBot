package systems.kinau.fishingbot.utils.nbt;

import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class CompoundTag extends Tag<Map<String, ? extends Tag<?>>> {

    public <U extends Tag<?>> U get(String name, Class<U> clazz) {
        Tag<?> tag = getValue().get(name);
        if (tag == null) return null;
        if (!tag.getClass().isAssignableFrom(clazz)) return null;
        return clazz.cast(tag);
    }

    public boolean containsKey(String name) {
        return getValue().containsKey(name);
    }

    public <U extends Tag<?>> boolean containsKey(String name, Class<U> clazz) {
        Tag<?> tag = getValue().get(name);
        if (tag == null) return false;
        return tag.getClass().isAssignableFrom(clazz);
    }

    @Override
    protected CompoundTag read(ByteArrayDataInputWrapper in) {
        Map<String, Tag<?>> value = new HashMap<>();
        try {
            while (true) {
                byte type = in.readByte();
                if (type == TagRegistry.TAG_END_ID) {
                    break;
                } else {
                    Tag<?> tag = readNextNamedTag(in, type);
                    value.put(tag.getName(), tag);
                }
            }
        } finally {
            setValue(value);
        }
        return this;
    }

    @Override
    public String toString(int tabs) {
        StringBuilder sb = new StringBuilder(addTabs(tabs) + getClass().getSimpleName() + " (" + Optional.ofNullable(getName()).orElse("") + "): ");
        for (Tag<?> value : getValue().values()) {
            sb.append("\n").append(value.toString(tabs + 1));
        }
        sb.append("\n").append(TagRegistry.createTag(EndTag.class).toString(tabs));
        return sb.toString();
    }
}
