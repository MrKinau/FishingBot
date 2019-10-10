/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5th 2019
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSetExperience extends Packet {
	
	@Getter private static int experience;
	@Getter private static int levels = -1;
	
	@Override
	public void write(ByteArrayDataOutput out, int protocolId) { }
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		in.readFloat();
		int newLevel = readVarInt(in);

		if(getLevels() >= 0 && getLevels() < newLevel) {
			MineBot.getLog().info("Achieved level " + newLevel);
			if(!MineBot.getConfig().getAnnounceLvlUp().equalsIgnoreCase("false"))
				networkHandler.sendPacket(new PacketOutChat(MineBot.getConfig().getAnnounceLvlUp().replace("%lvl%", String.valueOf(newLevel))));
		}

		PacketInSetExperience.levels = newLevel;
		PacketInSetExperience.experience = readVarInt(in);

	}
}
