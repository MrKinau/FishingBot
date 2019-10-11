/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.Random;

public class PacketOutLook extends Packet {

    private Random random = new Random();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        if(MineBot.getInstance().getPlayer() == null)
            return;
        out.writeFloat(random.nextInt(180));
        out.writeFloat(random.nextInt(180));
        out.writeBoolean(true);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {

    }
}
