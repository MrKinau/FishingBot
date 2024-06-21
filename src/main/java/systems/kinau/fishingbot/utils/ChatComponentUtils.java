/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/6
 */

package systems.kinau.fishingbot.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.util.ArrayList;
import java.util.List;

// TODO This is garbage
public class ChatComponentUtils {

    public static String toPlainText(JsonObject object) {
        StringBuilder messageBuilder = new StringBuilder();

        try {
            return getText(object, messageBuilder);
        } catch (Exception ignore) {}
        return null;
    }

    private static String getText(Object object, StringBuilder messageBuilder) {
        if (!(object instanceof JsonObject)) {
            messageBuilder.append(object.toString());
            return messageBuilder.toString();
        }

        JsonObject jObject = (JsonObject) object;

        if (jObject.has("text")) {
            String text = jObject.get("text").getAsString();
            if (!text.isEmpty()) messageBuilder.append(text);
        }

        if (jObject.has("extra") && jObject.get("extra").isJsonArray()) {
            JsonArray extras = jObject.getAsJsonArray("extra");

            for (int i = 0; i < extras.size(); i++) {
                if (extras.get(i).isJsonObject()) {
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

        if (jObject.has("translate")) {
            String translationKey = jObject.get("translate").getAsString();
            List<String> arguments = new ArrayList<>();
            if (jObject.has("with")) {
                JsonArray array = jObject.getAsJsonArray("with");
                for (Object argument : array) {
                    arguments.add(getText(argument, new StringBuilder()));
                }
            }
            messageBuilder.append(FishingBot.getInstance().getCurrentBot().getMinecraftTranslations().getTranslation(translationKey, arguments.toArray(new String[0])));
        }

        return messageBuilder.toString();
    }

    // If chat types changed in registry, this is not working
    // TODO load from networked registry
    public static String sillyTransformWithChatType(int protocolId, int chatType, String senderName, String recipientName, String message) {
        if (protocolId >= ProtocolConstants.MC_1_21)
            chatType--;
        if (chatType == 1) {
            return "* " + senderName + " " + message;
        } else if (chatType == 2) {
            return senderName + " whispers to you: " + message;
        } else if (chatType == 3) {
            return "You whisper to " + recipientName + ": " + message;
        } else if (chatType == 4) {
            return "[" + senderName + "] " + message;
        } else {
            return "<" + senderName + "> " + message;
        }
    }
}
