/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5th 2019
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.ChatHandler.ChatType;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.TextComponent;

public class PacketInChat extends Packet {
	
	private static final JsonParser PARSER = new JsonParser();
	
	@Override
	public void write(ByteArrayDataOutput out, int protocolId) { }
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		try {
			String minecraftJson = readString(in);

			JsonObject object = PARSER.parse(minecraftJson).getAsJsonObject();

			String text;
			//Thrown only on vanilla servers
			//TODO: Fix vanilla server json as text
			try {
				text = TextComponent.toPlainText(object);
			} catch (IllegalStateException ex) {
				text = minecraftJson;
			}
			FishingBot.getChatHandler().receiveMessage(text, ChatType.values()[in.readByte()], minecraftJson);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
