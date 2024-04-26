package systems.kinau.fishingbot.network.entity;

import lombok.Getter;
import org.apache.commons.lang3.function.TriFunction;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.function.BiFunction;
import java.util.function.Function;

public class EntityDataElement<T> {

    private final TriFunction<ByteArrayDataInputWrapper, NetworkHandler, Integer, T> readerFunction;
    @Getter
    private T value;
    @Getter
    private final String internalId;

    public EntityDataElement(String internalId, Function<ByteArrayDataInputWrapper, T> readerFunction) {
        this.internalId = internalId;
        this.readerFunction = (in, networkHandler, protocolId) -> readerFunction.apply(in);
    }

    public EntityDataElement(String internalId, BiFunction<ByteArrayDataInputWrapper, Integer, T> readerFunction) {
        this.internalId = internalId;
        this.readerFunction = (in, networkHandler, protocolId) -> readerFunction.apply(in, protocolId);
    }

    public EntityDataElement(String internalId, TriFunction<ByteArrayDataInputWrapper, NetworkHandler, Integer, T> readerFunction) {
        this.internalId = internalId;
        this.readerFunction = readerFunction;
    }

    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int protocolId) {
        this.value = readerFunction.apply(in, networkHandler, protocolId);
    }
}
