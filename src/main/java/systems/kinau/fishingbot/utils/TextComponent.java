/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/6
 */

package systems.kinau.fishingbot.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// TODO This is garbage
public class TextComponent {

    public static String toPlainText(JsonObject object) throws IllegalStateException {
        StringBuilder messageBuilder = new StringBuilder();

        if (object.has("with"))  {
            JsonArray array = object.getAsJsonArray("with");
            for (Object o : array) {
                messageBuilder = new StringBuilder(getText(o, messageBuilder) + " ");
            }
            if (object.has("translate") && object.get("translate").getAsString().equals("multiplayer.player.joined"))
                return messageBuilder + "joined the game";
            if (object.has("translate") && object.get("translate").getAsString().equals("multiplayer.player.left"))
                return messageBuilder + "left the game";
            return messageBuilder.toString();
        } else {
            return getText(object, messageBuilder);
        }
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
        return messageBuilder.toString();
    }
}
