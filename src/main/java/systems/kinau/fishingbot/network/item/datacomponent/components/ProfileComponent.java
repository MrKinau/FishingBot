package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.*;

@Getter
public class ProfileComponent extends DataComponent {

    private Optional<String> name;
    private Optional<UUID> uuid;
    private Properties properties;

    public ProfileComponent(int componentTypeId) {
        super(componentTypeId);
    }

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

    @Getter
    @NoArgsConstructor
    public static class Properties implements DataComponentPart {

        private List<Property> properties = Collections.emptyList();

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeVarInt(properties.size(), out);
            for (Property property : properties) {
                property.write(out, protocolId);
            }
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.properties = new LinkedList<>();
            int count = Packet.readVarInt(in);
            for (int i = 0; i < count; i++) {
                Property property = new Property();
                property.read(in, protocolId);
                this.properties.add(property);
            }
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Property implements DataComponentPart {

        private String name;
        private String value;
        private String signature;

        @Override
        public void write(ByteArrayDataOutput out, int protocolId) {
            Packet.writeString(name, out);
            Packet.writeString(value, out);
            out.writeBoolean(signature != null);
            if (signature != null)
                Packet.writeString(signature, out);
        }

        @Override
        public void read(ByteArrayDataInputWrapper in, int protocolId) {
            this.name = Packet.readString(in);
            this.value = Packet.readString(in);
            this.signature = null;
            if (in.readBoolean())
                signature = Packet.readString(in);
        }
    }
}
