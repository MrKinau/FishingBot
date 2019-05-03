/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.fishing;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ItemHandler {

    public static Map<Integer, String> itemsMap = new HashMap<>();

    public ItemHandler() {
        JsonElement root = null;
        root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries.json")));
        root = root.getAsJsonObject().get("minecraft:item").getAsJsonObject().get("entries").getAsJsonObject();
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject().entrySet()) {
            itemsMap.put(stringJsonElementEntry.getValue().getAsJsonObject().get("protocol_id").getAsInt(), stringJsonElementEntry.getKey());
        }
    }

    public static String getItemName(int id) {
        return itemsMap.get(id);
    }
}
