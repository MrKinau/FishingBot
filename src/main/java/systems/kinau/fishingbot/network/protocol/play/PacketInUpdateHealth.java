package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@NoArgsConstructor
public class PacketInUpdateHealth extends Packet {

    @Getter private float health;
    @Getter private int food;
    @Getter private float saturation;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.health = in.readFloat();
        this.food = readVarInt(in);
        this.saturation = in.readFloat();

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID(), getHealth(), getFood(), getSaturation()));
    }
}
