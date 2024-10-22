package systems.kinau.fishingbot.network.item.datacomponent.components.parts.jukebox;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.SoundEvent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@Getter
@NoArgsConstructor
public class JukeboxSong implements DataComponentPart {

    private int musicDiscId;

    private SoundEvent soundEvent;
    private NBTTag description;
    private float lengthInSeconds;
    private int comparatorOutput;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(musicDiscId, out);
        if (musicDiscId == 0) {
            soundEvent.write(out, protocolId);
            Packet.writeNBT(description, out);
            out.writeFloat(lengthInSeconds);
            Packet.writeVarInt(comparatorOutput, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.musicDiscId = Packet.readVarInt(in);
        if (musicDiscId == 0) {
            this.soundEvent = new SoundEvent();
            soundEvent.read(in, protocolId);
            this.description = Packet.readNBT(in, protocolId);
            this.lengthInSeconds = in.readFloat();
            this.comparatorOutput = Packet.readVarInt(in);
        }
    }
}