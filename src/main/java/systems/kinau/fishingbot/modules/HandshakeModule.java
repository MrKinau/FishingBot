/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.State;
import systems.kinau.fishingbot.network.protocol.handshake.PacketOutHandshake;

public class HandshakeModule extends Module {

    @Getter private String serverName;
    @Getter private int serverPort;

    public HandshakeModule(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void onEnable() {
        MineBot.getInstance().getNet().sendPacket(new PacketOutHandshake(serverName, serverPort));
        MineBot.getInstance().getNet().setState(State.LOGIN);
    }

    @Override
    public void onDisable() {
        MineBot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }
}
