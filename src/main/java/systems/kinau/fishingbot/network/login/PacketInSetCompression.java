/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class PacketInSetCompression extends Packet {

    @Getter private int threshold;

    @Override
    public void write(ByteArrayDataOutput out) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException {
        threshold = readVarInt(in);
        networkHandler.setThreshold(threshold);
        FishingBot.getLog().info("Changed current threshold to: " + threshold);
    }
}
