package systems.kinau.fishingbot.network.item.datacomponent.components.parts.profile;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponentPart;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.UUID;

public class StaticProfile implements DataComponentPart {

    private String name;
    private UUID uuid;
    private Properties properties;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        Packet.writeUUID(uuid, out);
        Packet.writeString(name, out);
        properties.write(out, protocolId);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        this.uuid = Packet.readUUID(in);
        this.name = Packet.readString(in);
        Properties properties = new Properties();
        properties.read(in, protocolId);
        this.properties = properties;
    }
}
