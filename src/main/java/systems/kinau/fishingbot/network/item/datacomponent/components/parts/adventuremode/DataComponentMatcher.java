package systems.kinau.fishingbot.network.item.datacomponent.components.parts.adventuremode;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class DataComponentMatcher implements DataComponentPart {

    private List<DataComponent> exactComponent = new ArrayList<>();
    private List<Integer> predicates = new ArrayList<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(exactComponent.size(), out);
        for (DataComponent dataComponent : exactComponent) {
            Packet.writeVarInt(dataComponent.getComponentTypeId(), out);
            dataComponent.write(out, protocolId);
        }
        Packet.writeVarInt(predicates.size(), out);
        for (Integer predicate : predicates) {
            Packet.writeVarInt(predicate, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        int count = Packet.readVarInt(in);
        this.exactComponent = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int componentType = Packet.readVarInt(in);
            DataComponent dataComponent = FishingBot.getInstance().getCurrentBot().getNet().getDataComponentRegistry().createComponent(componentType, protocolId);
            if (dataComponent != null) {
                dataComponent.read(in, protocolId);
                this.exactComponent.add(dataComponent);
            }
        }
        count = Packet.readVarInt(in);
        this.predicates = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int predicate = Packet.readVarInt(in);
            this.predicates.add(predicate);
        }
    }
}
