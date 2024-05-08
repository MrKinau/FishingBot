package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FireworksComponent extends DataComponent {

    private int flightDuration;
    private List<FireworkExplosion> explosions = Collections.emptyList();

    public FireworksComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(flightDuration, out);
        Packet.writeVarInt(explosions.size(), out);
        for (FireworkExplosion explosion : explosions) {
            explosion.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.flightDuration = Packet.readVarInt(in);
        this.explosions = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            FireworkExplosion explosion = new FireworkExplosion();
            explosion.read(in, protocolId);
            explosions.add(explosion);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class FireworkExplosion implements DataComponentPart {

        private int shape;
        private int[] colors;
        private int[] fadeColors;
        private boolean hasTrail;
        private boolean hasTwinkle;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(shape, out);

            Packet.writeVarInt(colors == null ? 0 : colors.length, out);
            for (int color : colors) {
                out.writeInt(color);
            }

            Packet.writeVarInt(fadeColors == null ? 0 : fadeColors.length, out);
            for (int fadeColor : fadeColors) {
                out.writeInt(fadeColor);
            }

            out.writeBoolean(hasTrail);
            out.writeBoolean(hasTwinkle);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.shape = Packet.readVarInt(in);

            int count = Packet.readVarInt(in);
            this.colors = new int[count];
            for (int i = 0; i < count; i++) {
                colors[i] = in.readInt();
            }

            count = Packet.readVarInt(in);
            this.fadeColors = new int[count];
            for (int i = 0; i < count; i++) {
                fadeColors[i] = in.readInt();
            }

            this.hasTrail = in.readBoolean();
            this.hasTwinkle = in.readBoolean();
        }
    }
}
