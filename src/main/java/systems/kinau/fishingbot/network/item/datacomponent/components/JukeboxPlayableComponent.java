package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.jukebox.JukeboxSong;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

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
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1)
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
        if (protocolId < ProtocolConstants.MC_1_21_5_RC_1)
            this.showInTooltip = in.readBoolean();
    }
}
