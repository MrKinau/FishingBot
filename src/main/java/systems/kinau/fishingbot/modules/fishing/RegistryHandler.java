/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.modules.fishing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.enums.MaterialMc18;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

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
    public static Map<Integer, String> itemsMap_1_19_3 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_19_4 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_20 = new HashMap<>();
    public static Map<Integer, String> itemsMap_1_20_3 = new HashMap<>();

    public static Map<String, Integer> entitiesMap_1_14 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_15 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_16 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_16_2 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_17 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_18 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_19 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_19_3 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_19_4 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_20 = new HashMap<>();
    public static Map<String, Integer> entitiesMap_1_20_3 = new HashMap<>();

    public RegistryHandler(int protocolId) {
        JsonObject root = null;
        try {
            switch (protocolId) {
                case ProtocolConstants.MINECRAFT_1_13: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_13.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_13_1:
                case ProtocolConstants.MINECRAFT_1_13_2: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_13_1.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_14:
                case ProtocolConstants.MINECRAFT_1_14_1:
                case ProtocolConstants.MINECRAFT_1_14_2:
                case ProtocolConstants.MINECRAFT_1_14_3:
                case ProtocolConstants.MINECRAFT_1_14_4: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_14.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_15:
                case ProtocolConstants.MINECRAFT_1_15_1:
                case ProtocolConstants.MINECRAFT_1_15_2: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_15.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_16:
                case ProtocolConstants.MINECRAFT_1_16_1: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_16.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_16_2:
                case ProtocolConstants.MINECRAFT_1_16_3:
                case ProtocolConstants.MINECRAFT_1_16_4: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_16_2.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_17:
                case ProtocolConstants.MINECRAFT_1_17_1: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_17.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_18:
                case ProtocolConstants.MINECRAFT_1_18_2: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_18.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_19:
                case ProtocolConstants.MINECRAFT_1_19_1: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_19.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_19_3: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_19_3.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_19_4: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_19_4.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_20:
                case ProtocolConstants.MINECRAFT_1_20_2: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_20.json"))).getAsJsonObject();
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_20_3:
                default: {
                    root = new JsonParser().parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("registries_1_20_3.json"))).getAsJsonObject();
                    break;
                }
            }
        } catch (JsonParseException ignore) {
        }

        if (root == null)
            return;
        JsonObject finalRoot = root;
        if (protocolId < ProtocolConstants.MINECRAFT_1_14) {
            finalRoot.keySet().forEach(key -> {
                getItemsMap(protocolId).put(finalRoot.getAsJsonObject(key).get("protocol_id").getAsInt(), key);
            });
            return;
        }

        // items
        JsonObject items = finalRoot.getAsJsonObject("minecraft:item").getAsJsonObject("entries");
        items.keySet().forEach(key -> {
            getItemsMap(protocolId).put(items.getAsJsonObject(key).get("protocol_id").getAsInt(), key);
        });

        // entities
        JsonObject entities = finalRoot.getAsJsonObject("minecraft:entity_type").getAsJsonObject("entries");
        entities.keySet().forEach(key -> {
            getEntitiesMap(protocolId).put(key, entities.getAsJsonObject(key).get("protocol_id").getAsInt());
        });
    }

    public static String getItemName(int id, int protocol) {
        return getItemsMap(protocol).getOrDefault(id, "Modded Item");
    }

    public static int getEntityType(String entityName, int protocol) {
        return getEntitiesMap(protocol).getOrDefault(entityName, 0);
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
        else if (protocol >= ProtocolConstants.MINECRAFT_1_19 && protocol <= ProtocolConstants.MINECRAFT_1_19_1)
            return itemsMap_1_19;
        else if (protocol == ProtocolConstants.MINECRAFT_1_19_3)
            return itemsMap_1_19_3;
        else if (protocol == ProtocolConstants.MINECRAFT_1_19_4)
            return itemsMap_1_19_4;
        else if (protocol <= ProtocolConstants.MINECRAFT_1_20_2)
            return itemsMap_1_20;
        else
            return itemsMap_1_20_3;
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
        else if (protocol >= ProtocolConstants.MINECRAFT_1_19 && protocol <= ProtocolConstants.MINECRAFT_1_19_1)
            return entitiesMap_1_19;
        else if (protocol == ProtocolConstants.MINECRAFT_1_19_3)
            return entitiesMap_1_19_3;
        else if (protocol <= ProtocolConstants.MINECRAFT_1_20_2)
            return entitiesMap_1_20;
        else
            return entitiesMap_1_20_3;
    }
}
