/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/6
 */

package systems.kinau.fishingbot.network.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TextComponent {

    public static String toPlainText(JsonObject object) {
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
        return messageBuilder.toString();
    }
}
