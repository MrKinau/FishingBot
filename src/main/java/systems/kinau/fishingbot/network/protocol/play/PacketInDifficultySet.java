/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.util.Arrays;

@NoArgsConstructor
public class PacketInDifficultySet extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Arrays.asList(MineBot.getConfig().getStartText().split(";")).forEach(s -> {
                MineBot.getInstance().getNet().sendPacket(new PacketOutChat(s.replace("%prefix%", MineBot.PREFIX)));
            });
            MineBot.getInstance().getManager().onConnected();
        }).start();
    }
}
