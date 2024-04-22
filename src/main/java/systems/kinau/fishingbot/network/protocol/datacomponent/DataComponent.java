package systems.kinau.fishingbot.network.protocol.datacomponent;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@RequiredArgsConstructor
public abstract class DataComponent {

    private final int componentTypeId;

    public abstract void write(ByteArrayDataOutput out, int protocolId);

    public abstract void read(ByteArrayDataInputWrapper in, int protocolId);
}
