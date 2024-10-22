package systems.kinau.fishingbot.network.item.datacomponent.components.parts.instrument;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@Getter
@NoArgsConstructor
public class Instrument implements DataComponentPart {

    private int instrumentId;
    private SoundEvent soundEvent;
    private int useDuration;
    private float range;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(instrumentId, out);
        if (instrumentId == 0) {
            soundEvent.write(out, protocolId);
            Packet.writeVarInt(useDuration, out);
            out.writeFloat(range);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.instrumentId = Packet.readVarInt(in);
        if (instrumentId == 0) {
            this.soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.useDuration = Packet.readVarInt(in);
            this.range = in.readFloat();
        }
    }
}