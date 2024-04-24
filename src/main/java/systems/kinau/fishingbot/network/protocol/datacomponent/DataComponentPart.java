package systems.kinau.fishingbot.network.protocol.datacomponent;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public interface DataComponentPart {

    void write(ByteArrayDataOutput out, int protocolId);

    void read(ByteArrayDataInputWrapper in, int protocolId);

}
