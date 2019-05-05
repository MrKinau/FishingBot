/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.protocol.handshake;

import lombok.Getter;
import systems.kinau.fishingbot.network.Module;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.State;

public class HandshakeModule extends Module {

    @Getter private String serverName;
    @Getter private int serverPort;

    public HandshakeModule(String serverName, int serverPort, NetworkHandler networkHandler) {
        super(networkHandler);
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public void perform() {
        getNetworkHandler().sendPacket(new PacketHandshake(serverName, serverPort));
        getNetworkHandler().setState(State.LOGIN);
    }
}
