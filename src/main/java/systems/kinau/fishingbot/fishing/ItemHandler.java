/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.fishing;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ItemHandler {

    public static Map<Integer, String> itemsMap_1_13 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_13_1 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_14 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_15 = new HashMap<>();

    public ItemHandler(int protocolId) {
        JsonElement root = null;
        switch(protocolId) {
            case ProtocolConstants.MINECRAFT_1_13: {
                root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_13.json")));
                root = root.getAsJsonObject();
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject().entrySet()) {
                    itemsMap_1_13.put(stringJsonElementEntry.getValue().getAsJsonObject().get("protocol_id").getAsInt(), stringJsonElementEntry.getKey());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1: {
                root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_13_1.json")));
                root = root.getAsJsonObject();
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject().entrySet()) {
                    itemsMap_1_13_1.put(stringJsonElementEntry.getValue().getAsJsonObject().get("protocol_id").getAsInt(), stringJsonElementEntry.getKey());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:{
                root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_14.json")));
                root = root.getAsJsonObject().get("minecraft:item").getAsJsonObject().get("entries").getAsJsonObject();
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject().entrySet()) {
                    itemsMap_1_14.put(stringJsonElementEntry.getValue().getAsJsonObject().get("protocol_id").getAsInt(), stringJsonElementEntry.getKey());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_15:
            default: {
                root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_15.json")));
                root = root.getAsJsonObject().get("minecraft:item").getAsJsonObject().get("entries").getAsJsonObject();
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject().entrySet()) {
                    itemsMap_1_15.put(stringJsonElementEntry.getValue().getAsJsonObject().get("protocol_id").getAsInt(), stringJsonElementEntry.getKey());
                }
                break;
            }
        }
    }

    public static String getItemName(int id, int protocol) {
        if(protocol == ProtocolConstants.MINECRAFT_1_13)
            return itemsMap_1_13.get(id);
        else if(protocol >= ProtocolConstants.MINECRAFT_1_13_1 && protocol <= ProtocolConstants.MINECRAFT_1_13_2)
            return itemsMap_1_13_1.get(id);
        else if(protocol >= ProtocolConstants.MINECRAFT_1_14 && protocol <= ProtocolConstants.MINECRAFT_1_14_4)
            return itemsMap_1_14.get(id);
        else
            return itemsMap_1_15.get(id);
    }
}
