package systems.kinau.fishingbot.network.item.datacomponent.components.parts.profile;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Optional;
import java.util.UUID;

public class DynamicProfile implements DataComponentPart {

    private Optional<String> name;
    private Optional<UUID> uuid;
    private Properties properties;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        out.writeBoolean(name.isPresent());
        name.ifPresent(name -> Packet.writeString(name, out));
        out.writeBoolean(uuid.isPresent());
        uuid.ifPresent(uuid -> Packet.writeUUID(uuid, out));
        properties.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        if (in.readBoolean()) {
            this.name = Optional.of(Packet.readString(in));
        } else {
            this.name = Optional.empty();
        }

        if (in.readBoolean()) {
            this.uuid = Optional.of(Packet.readUUID(in));
        } else {
            this.uuid = Optional.empty();
        }

        Properties properties = new Properties();
        properties.read(in, protocolId);
        this.properties = properties;
    }
}
