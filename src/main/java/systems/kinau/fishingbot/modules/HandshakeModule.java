/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.ProtocolState;
import systems.kinau.fishingbot.network.protocol.handshake.PacketOutHandshake;

@Getter
public class HandshakeModule extends Module {

    private String serverName;
    private int serverPort;

    public HandshakeModule(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutHandshake(serverName, serverPort));
        FishingBot.getInstance().getCurrentBot().getNet().setState(ProtocolState.LOGIN);
    }

    @Override
    public void onDisable() {
        FishingBot.getI18n().warning("module-handshake-disabling");
    }
}
