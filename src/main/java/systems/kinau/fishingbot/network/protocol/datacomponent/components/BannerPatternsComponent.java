package systems.kinau.fishingbot.network.protocol.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class BannerPatternsComponent extends DataComponent {

    private List<BannerPattern> patterns = Collections.emptyList();

    public BannerPatternsComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(patterns.size(), out);
        for (BannerPattern pattern : patterns) {
            pattern.write(out, protocolId);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.patterns = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            BannerPattern bannerPattern  = new BannerPattern();
            bannerPattern.read(in, protocolId);
            patterns.add(bannerPattern);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BannerPattern implements DataComponentPart {
        private int pattern;
        private int dyeColor;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(pattern, out);
            Packet.writeVarInt(dyeColor, out);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.pattern = Packet.readVarInt(in);
            this.dyeColor = Packet.readVarInt(in);
        }
    }
}
