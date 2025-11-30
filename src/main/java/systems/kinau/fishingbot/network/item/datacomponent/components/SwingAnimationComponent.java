package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class SwingAnimationComponent extends DataComponent {

    private int animationType;
    private int animationDuration;

    public SwingAnimationComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeVarInt(animationType, out);
        Packet.writeVarInt(animationDuration, out);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.animationType = Packet.readVarInt(in);
        this.animationDuration = Packet.readVarInt(in);
    }
}
