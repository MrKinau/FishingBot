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
import lombok.Getter;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.TextComponent;

@NoArgsConstructor
public class PacketInChat extends Packet {

	@Getter private String text;
	private final JsonParser PARSER = new JsonParser();

	@Override
	public void write(ByteArrayDataOutput out, int protocolId) {
		//Only incoming packet
	}
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		this.text = readString(in);
		try {
			JsonObject object = PARSER.parse(text).getAsJsonObject();

			try {
				this.text = TextComponent.toPlainText(object);
			} catch (Exception ignored) {
				//Ignored
			}

			FishingBot.getInstance().getEventManager().callEvent(new ChatEvent(getText()));
		} catch (Exception ignored) {
			//Ignored
		}
	}
}
