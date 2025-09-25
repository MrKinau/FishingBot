/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/6
 */

package systems.kinau.fishingbot.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
        if (object instanceof JsonPrimitive) {
            messageBuilder.append(((JsonPrimitive) object).getAsString());
            return messageBuilder.toString();
        }
        if (!(object instanceof JsonObject)) {
            messageBuilder.append(object.toString());
            return messageBuilder.toString();
        }

        JsonObject jObject = (JsonObject) object;

        String singleComponent = readSingleComponent(jObject);
        if (singleComponent != null) messageBuilder.append(singleComponent);

        if (jObject.has("extra") && jObject.get("extra").isJsonArray()) {
            JsonArray extras = jObject.getAsJsonArray("extra");
            for (int i = 0; i < extras.size(); i++) {
                String extraText = getText(extras.get(i), new StringBuilder());
                if (!extraText.isEmpty()) messageBuilder.append(extraText);
            }
        }

        return messageBuilder.toString();
    }

    private static String readSingleComponent(JsonObject object) {
        if (object.has("text")) {
            String text = object.get("text").getAsString();
            if (!text.isEmpty()) return text;
        }

        if (object.has("translate")) {
            String translationKey = object.get("translate").getAsString();
            List<String> arguments = new ArrayList<>();
            if (object.has("with")) {
                JsonArray array = object.getAsJsonArray("with");
                for (Object argument : array) {
                    arguments.add(getText(argument, new StringBuilder()));
                }
            }
            return FishingBot.getInstance().getCurrentBot().getMinecraftTranslations().getTranslation(translationKey, arguments.toArray(new String[0]));
        }

        return null;
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
