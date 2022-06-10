/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.modules.fishing;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.enums.MaterialMc18;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegistryHandler {

    public static Map<Integer, String> itemsMap_1_13 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_13_1 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_14 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_15 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_16 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_16_2 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_17 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_18 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_19 = new HashMap<>();

    public static Map<String, Integer> entitiesMap_1_14 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_15 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_16 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_16_2 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_17 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_18 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_19 = new HashMap<>();

    public RegistryHandler(int protocolId) {
        JSONObject root = null;
        try {
            switch (protocolId) {
                case ProtocolConstants.MINECRAFT_1_13: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_13.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_13_1:
                case ProtocolConstants.MINECRAFT_1_13_2: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_13_1.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_14:
                case ProtocolConstants.MINECRAFT_1_14_1:
                case ProtocolConstants.MINECRAFT_1_14_2:
                case ProtocolConstants.MINECRAFT_1_14_3:
                case ProtocolConstants.MINECRAFT_1_14_4: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_14.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_15:
                case ProtocolConstants.MINECRAFT_1_15_1:
                case ProtocolConstants.MINECRAFT_1_15_2: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_15.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_16:
                case ProtocolConstants.MINECRAFT_1_16_1: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_16.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_16_2:
                case ProtocolConstants.MINECRAFT_1_16_3:
                case ProtocolConstants.MINECRAFT_1_16_4: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_16_2.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_17:
                case ProtocolConstants.MINECRAFT_1_17_1: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_17.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_18:
                case ProtocolConstants.MINECRAFT_1_18_2: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_18.json")));
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_19:
                default: {
                    root = (JSONObject) new JSONParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_19.json")));
                    break;
                }
            }
        } catch (ParseException | IOException ignore) {
        }

        if (root == null)
            return;
        if (protocolId < ProtocolConstants.MINECRAFT_1_14) {
            root.forEach((key, value) -> {
                getItemsMap(protocolId).put(((Long) ((JSONObject) value).get("protocol_id")).intValue(), (String) key);
            });
            return;
        }

        // items
        JSONObject items = (JSONObject) ((JSONObject) root.get("minecraft:item")).get("entries");
        items.forEach((key, value) -> {
            getItemsMap(protocolId).put(((Long) ((JSONObject) value).get("protocol_id")).intValue(), (String) key);
        });

        //entities
        JSONObject entities = (JSONObject) ((JSONObject) root.get("minecraft:entity_type")).get("entries");
        entities.forEach((key, value) -> {
            getEntitiesMap(protocolId).put((String) key, ((Long) ((JSONObject) value).get("protocol_id")).intValue());
        });
    }

    public static String getItemName(int id, int protocol) {
        return getItemsMap(protocol).get(id);
    }

    public static int getEntityType(String entityName, int protocol) {
        return getEntitiesMap(protocol).get(entityName);
    }

    public static String getImageUrl(Item item) {
        String fileType = (item.getEnchantments() == null || item.getEnchantments().isEmpty()) ? "png" : "gif";
        return String.format("https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/items/%s." + fileType, item.getName().toLowerCase()).replace(" ", "%20");
    }

    public static Map<Integer, String> getItemsMap(int protocol) {
        if (protocol < ProtocolConstants.MINECRAFT_1_13) {
            Map<Integer, String> itemsMap = new HashMap<>();
            Arrays.stream(MaterialMc18.values()).forEach(materialMc18 -> {
                itemsMap.put(materialMc18.getId(), materialMc18.name());
            });
            return itemsMap;
        } else if (protocol == ProtocolConstants.MINECRAFT_1_13)
            return itemsMap_1_13;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_13_1 && protocol <= ProtocolConstants.MINECRAFT_1_13_2)
            return itemsMap_1_13_1;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_14 && protocol <= ProtocolConstants.MINECRAFT_1_14_4)
            return itemsMap_1_14;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_15 && protocol <= ProtocolConstants.MINECRAFT_1_15_2)
            return itemsMap_1_15;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_16 && protocol <= ProtocolConstants.MINECRAFT_1_16_1)
            return itemsMap_1_16;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_16_3 && protocol <= ProtocolConstants.MINECRAFT_1_16_4)
            return itemsMap_1_16_2;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_17 && protocol <= ProtocolConstants.MINECRAFT_1_17_1)
            return itemsMap_1_17;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_18 && protocol <= ProtocolConstants.MINECRAFT_1_18_2)
            return itemsMap_1_18;
        else
            return itemsMap_1_19;
    }

    public static Map<String, Integer> getEntitiesMap(int protocol) {
        if (protocol < ProtocolConstants.MINECRAFT_1_14)
            return Collections.emptyMap();
        else if (protocol <= ProtocolConstants.MINECRAFT_1_14_4)
            return entitiesMap_1_14;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_15 && protocol <= ProtocolConstants.MINECRAFT_1_15_2)
            return entitiesMap_1_15;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_16 && protocol <= ProtocolConstants.MINECRAFT_1_16_1)
            return entitiesMap_1_16;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_16_3 && protocol <= ProtocolConstants.MINECRAFT_1_16_4)
            return entitiesMap_1_16_2;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_17 && protocol <= ProtocolConstants.MINECRAFT_1_17_1)
            return entitiesMap_1_17;
        else if (protocol >= ProtocolConstants.MINECRAFT_1_18 && protocol <= ProtocolConstants.MINECRAFT_1_18_2)
            return entitiesMap_1_18;
        else
            return entitiesMap_1_19;
    }
}
