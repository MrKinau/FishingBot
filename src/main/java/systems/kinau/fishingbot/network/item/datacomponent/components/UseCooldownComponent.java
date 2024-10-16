package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;

public class UseCooldownComponent extends DataComponent {

    private float seconds;
    private Optional<String> cooldownGroup = Optional.empty();

    public UseCooldownComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeFloat(seconds);
        if (cooldownGroup.isPresent()) {
            out.writeBoolean(true);
            out.writeUTF(cooldownGroup.get());
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.seconds = in.readFloat();
        if (in.readBoolean()) {
            this.cooldownGroup = Optional.of(in.readUTF());
        } else {
            this.cooldownGroup = Optional.empty();
        }
    }
}
