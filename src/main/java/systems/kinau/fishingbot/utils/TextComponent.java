/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/6
 */

package systems.kinau.fishingbot.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TextComponent {

    public static String toPlainText(JSONObject object) throws IllegalStateException {
        StringBuilder messageBuilder = new StringBuilder();

        if (object.containsKey("with"))  {
            JSONArray array = (JSONArray) object.get("with");
            for (Object o : array) {
                messageBuilder = new StringBuilder(getText(o, messageBuilder) + " ");
            }
            if (object.containsKey("translate") && object.get("translate").toString().equals("multiplayer.player.joined"))
                return messageBuilder.toString() + "joined the game";
            if (object.containsKey("translate") && object.get("translate").toString().equals("multiplayer.player.left"))
                return messageBuilder.toString() + "left the game";
            return messageBuilder.toString();
        } else {
            return getText(object, messageBuilder);
        }
    }

    private static String getText(Object object, StringBuilder messageBuilder) {
        if (!(object instanceof JSONObject)) {
            messageBuilder.append(object.toString());
            return messageBuilder.toString();
        }

        JSONObject jObject = (JSONObject) object;

        if (jObject.containsKey("text")) {
            String text = (String) jObject.get("text");
            if (!text.isEmpty()) messageBuilder.append(text);
        }

        if (jObject.containsKey("extra") && jObject.get("extra") instanceof JSONArray) {
            JSONArray extras = (JSONArray) jObject.get("extra");

            for (int i = 0; i < extras.size(); i++) {
                if (extras.get(i) instanceof JSONObject) {
                    JSONObject extraObject = (JSONObject) extras.get(i);

                    if (extraObject.containsKey("text")) {
                        String text = (String) extraObject.get("text");
                        if (!text.isEmpty()) messageBuilder.append(text);
                    }
                } else {
                    messageBuilder.append(extras.get(i).toString());
                }
            }
        }
        return messageBuilder.toString();
    }
}
