package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class InstrumentComponent extends DataComponent {

    private Instrument instrument;

    public InstrumentComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        instrument.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.instrument = new Instrument();
        instrument.read(in, protocolId);
    }

    @Getter
    @NoArgsConstructor
    public static class Instrument implements DataComponentPart {
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

    @Getter
    @NoArgsConstructor
    public static class SoundEvent implements DataComponentPart {
        private int soundEventId;
        private String resourceLocation;
        private Optional<Float> fixedRange;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(soundEventId, out);
            if (soundEventId == 0) {
                Packet.writeString(resourceLocation, out);
                if (fixedRange.isPresent())
                    out.writeFloat(fixedRange.get());
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.soundEventId = Packet.readVarInt(in);
            if (soundEventId == 0) {
                this.resourceLocation = Packet.readString(in);
                if (in.readBoolean())
                    fixedRange = Optional.of(in.readFloat());
                else
                    fixedRange = Optional.empty();
            }
        }
    }
}
