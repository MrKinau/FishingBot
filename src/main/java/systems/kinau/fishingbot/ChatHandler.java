/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5h 2019
 */

package systems.kinau.fishingbot;

import systems.kinau.fishingbot.network.protocol.play.PacketInSetExperience;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;

public class ChatHandler {
	
	private FishingBot instance;
	
	public ChatHandler(FishingBot instance) {
		this.instance = instance;
	}
	
	public void receiveMessage(String content, ChatType chatType, String minecraftJson) {
		if (content.contains(instance.getAuthData().getUsername() + ", Level?")) {
			instance.getNet().sendPacket(new PacketOutChat(PacketInSetExperience.getLevels() + " Level, Sir!"));
		}
	}
	
	public enum ChatType {
		CHAT,
		SYSTEM_MESSAGE,
		ACTION_BAR;
		
		public static final ChatType[] values = values();
	}
	
}
