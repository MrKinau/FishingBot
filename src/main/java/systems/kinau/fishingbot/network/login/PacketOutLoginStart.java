/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutLoginStart extends Packet {

    private String userName;

    @Override
    public void write(ByteArrayDataOutput out) throws IOException {
        PacketHelper.writeString(out, userName);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException { }
}
