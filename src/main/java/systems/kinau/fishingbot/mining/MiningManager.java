package systems.kinau.fishingbot.mining;

import systems.kinau.fishingbot.Manager;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

public class MiningManager extends Manager {

    @Override
    public void onConnected() {
        synchronized (MineBot.getLog()) {
            MineBot.getLog().info("Starting mining!");
            if(MineBot.getServerProtocol() == ProtocolConstants.MINECRAFT_1_8)
                startPositionUpdate(getNetworkHandler());
        }
    }

    @Override
    public void tick() {
        MineBot.getInstance().getPlayer().tick();
    }
}
