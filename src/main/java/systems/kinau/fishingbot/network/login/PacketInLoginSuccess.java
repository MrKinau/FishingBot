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
import systems.kinau.fishingbot.network.State;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.PacketHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketInLoginSuccess extends Packet {

    @Getter private UUID uuid;
    @Getter private String userName;

    @Override
    public void write(ByteArrayDataOutput out) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) throws IOException {
        String uuidStr = PacketHelper.readString(in).replace("-","");
        this.uuid = new UUID(new BigInteger(uuidStr.substring(0, 16), 16).longValue(),new BigInteger(uuidStr.substring(16), 16).longValue());
        this.userName = PacketHelper.readString(in);
        FishingBot.getLog().info("Login successful!");
        FishingBot.getLog().info("Name: " + userName);
        FishingBot.getLog().info("UUID: " + uuid);
        networkHandler.setState(State.PLAY);
    }
}
