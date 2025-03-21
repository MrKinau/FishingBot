package systems.kinau.fishingbot.network.item.datacomponent.components.parts.instrument;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@Getter
@NoArgsConstructor
public class Instrument implements DataComponentPart {

    private boolean instrumentDefined;
    private int instrumentId;
    private SoundEvent soundEvent;
    private int useDuration;
    private float useDurationNew;
    private float range;
    private NBTTag description;
    private String registryKey;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1 || instrumentDefined) {
            out.writeBoolean(true);
            Packet.writeVarInt(instrumentId, out);
            if (instrumentId == 0) {
                soundEvent.write(out, protocolId);
                if (protocolId >= ProtocolConstants.MC_1_21_2)
                    out.writeFloat(useDurationNew);
                else
                    Packet.writeVarInt(useDuration, out);
                out.writeFloat(range);
                if (protocolId >= ProtocolConstants.MC_1_21_2)
                    Packet.writeNBT(description, out);
            }
        } else {
            out.writeBoolean(false);
            Packet.writeString(registryKey, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1 || (this.instrumentDefined = in.readBoolean())) {
            this.instrumentId = Packet.readVarInt(in);
            if (instrumentId == 0) {
                this.soundEvent = new SoundEvent();
                soundEvent.read(in, protocolId);
                if (protocolId >= ProtocolConstants.MC_1_21_2)
                    this.useDurationNew = in.readFloat();
                else
                    this.useDuration = Packet.readVarInt(in);
                this.range = in.readFloat();
                if (protocolId >= ProtocolConstants.MC_1_21_2)
                    this.description = Packet.readNBT(in, protocolId);
            }
        } else {
            this.registryKey = Packet.readString(in);
        }
    }
}