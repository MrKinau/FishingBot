package systems.kinau.fishingbot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.fireworks.FireworkExplosion;
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
}
