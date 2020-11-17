/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.login.LoginSuccessEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketInLoginSuccess extends Packet {

    @Getter private UUID uuid;
    @Getter private String userName;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {

        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() < ProtocolConstants.MINECRAFT_1_16) {
            String uuidStr = readString(in).replace("-", "");
            this.uuid = new UUID(new BigInteger(uuidStr.substring(0, 16), 16).longValue(), new BigInteger(uuidStr.substring(16), 16).longValue());
            this.userName = readString(in);
        } else {
            this.uuid = readUUID(in);
            this.userName = readString(in);
        }

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new LoginSuccessEvent(uuid, userName));
    }
}
