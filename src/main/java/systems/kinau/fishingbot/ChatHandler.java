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

import java.util.Scanner;

public class ChatHandler {
	
	private MineBot instance;
    private Thread chatThread;
	
	public ChatHandler(MineBot instance) {
		this.instance = instance;
		if(MineBot.getConfig().isProxyChat()) {
			chatThread = new Thread(() -> {
				Scanner scanner = new Scanner(System.in);
				while(true){
					String line = scanner.nextLine();
					instance.getNet().sendPacket(new PacketOutChat(line));
				}
			});
			chatThread.setDaemon(true);
			chatThread.start();
		}
	}
	
	public void receiveMessage(String content, ChatType chatType, String minecraftJson) {
		if (content.contains(instance.getAuthData().getUsername() + ", Level?")) {
			instance.getNet().sendPacket(new PacketOutChat(PacketInSetExperience.getLevels() + " Level, Sir!"));
		}
		if (MineBot.getConfig().isProxyChat() && !"".equals(content)) {
			MineBot.getLog().info(content);
		}
	}
	
	public enum ChatType {
		CHAT,
		SYSTEM_MESSAGE,
		ACTION_BAR;
		
		public static final ChatType[] values = values();
	}
	
}
