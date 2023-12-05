package systems.kinau.fishingbot.utils.nbt;

import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter(AccessLevel.PROTECTED)
public abstract class Tag<T> {

    private String name;
    private T value;

    protected <U extends Tag<?>> U readNextNamedTag(ByteArrayDataInputWrapper in, byte type) {
        return (U) TagRegistry.createTag(type).readNamed(in);
    }

    protected <U extends Tag<?>> U readNextTag(ByteArrayDataInputWrapper in, Class<U> type) {
        return (U) TagRegistry.createTag(type).read(in);
    }

    protected <U extends Tag<?>> U readNextTag(ByteArrayDataInputWrapper in, byte type) {
        return (U) TagRegistry.createTag(type).read(in);
    }

    protected void readName(ByteArrayDataInputWrapper in) {
        short length = in.readShort();
        readName(length, in);
    }

    protected void readName(int length, ByteArrayDataInputWrapper in) {
        if (length <= 0) {
            setName(null);
            return;
        }
        byte[] name = new byte[length];
        in.readBytes(name);
        setName(new String(name, StandardCharsets.UTF_8));
    }

    protected Tag<T> readNamed(ByteArrayDataInputWrapper in) {
        readName(in);
        return read(in);
    }

    protected abstract Tag<T> read(ByteArrayDataInputWrapper in);

    protected String addTabs(int tabs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabs; i++)
            sb.append('\t');
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int tabs) {
        return addTabs(tabs) + getClass().getSimpleName() + " (" + Optional.ofNullable(getName()).orElse("") + "): " + Optional.ofNullable(getValue()).map(Objects::toString).orElse("");
    }

    public abstract JsonElement toJson();
}
