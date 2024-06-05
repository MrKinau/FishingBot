package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@Getter
public class JukeboxPlayableComponent extends DataComponent {

    private JukeboxSong song;
    private String songResourceLocation;
    private boolean showInTooltip;

    public JukeboxPlayableComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(song != null);
        if (song != null) {
            song.write(out, protocolId);
        } else {
            Packet.writeString(songResourceLocation, out);
        }
        out.writeBoolean(showInTooltip);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        boolean fullJukeboxSong = in.readBoolean();
        if (fullJukeboxSong) {
            this.song = new JukeboxSong();
            song.read(in, protocolId);
        } else {
            this.songResourceLocation = Packet.readString(in);
        }
        this.showInTooltip = in.readBoolean();
    }

    @Getter
    @NoArgsConstructor
    public static class JukeboxSong implements DataComponentPart {
        private int musicDiscId;

        private InstrumentComponent.SoundEvent soundEvent;
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
                this.soundEvent = new InstrumentComponent.SoundEvent();
                soundEvent.read(in, protocolId);
                this.description = Packet.readNBT(in, protocolId);
                this.lengthInSeconds = in.readFloat();
                this.comparatorOutput = Packet.readVarInt(in);
            }
        }
    }
}
