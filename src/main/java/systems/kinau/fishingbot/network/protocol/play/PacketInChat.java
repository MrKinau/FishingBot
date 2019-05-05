/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5th 2019
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.ChatHandler.ChatType;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInChat extends Packet {
	
	private static final JsonParser PARSER = new JsonParser();
	
	@Override
	public void write(ByteArrayDataOutput out, int protocolId) { }
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		try {
			String minecraftJson = readString(in);

			JsonObject object = PARSER.parse(minecraftJson).getAsJsonObject();
			StringBuilder messageBuilder = new StringBuilder();

			if (object.has("text")) {
				String text = object.get("text").getAsString();
				if (!text.isEmpty()) messageBuilder.append(text);
			}

			if (object.has("extra") && object.get("extra").isJsonArray()) {
				JsonArray extras = object.getAsJsonArray("extra");

				for (int i = 0; i < extras.size(); i++) {
					if(extras.get(i).isJsonObject()) {
						JsonObject extraObject = extras.get(i).getAsJsonObject();

						if (extraObject.has("text")) {
							String text = extraObject.get("text").getAsString();
							if (!text.isEmpty()) messageBuilder.append(text);
						}
					} else {
						messageBuilder.append(extras.get(i).getAsString());
					}
				}
			}

			FishingBot.getChatHandler().receiveMessage(messageBuilder.toString(), ChatType.values()[in.readByte()], minecraftJson);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
