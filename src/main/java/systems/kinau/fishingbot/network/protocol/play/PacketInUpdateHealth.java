/*
 * Created by David Luedtke (MrKinau)
 * 2020/2/8
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

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

        MineBot.getInstance().getEventManager().callEvent(new UpdateHealthEvent(health, food, saturation));
    }
}
