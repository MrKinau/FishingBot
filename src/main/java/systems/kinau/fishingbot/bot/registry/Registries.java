package systems.kinau.fishingbot.bot.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.bot.registry.registries.DataComponentTypeRegistry;
import systems.kinau.fishingbot.bot.registry.registries.EnchantmentRegistry;
import systems.kinau.fishingbot.bot.registry.registries.EntityTypeRegistry;
import systems.kinau.fishingbot.bot.registry.registries.ItemRegistry;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Registries {

    private static Registries registries;

    public static ItemRegistry ITEM;
    public static EntityTypeRegistry ENTITY_TYPE;
    public static EnchantmentRegistry ENCHANTMENT;
    public static DataComponentTypeRegistry DATA_COMPONENT_TYPE;

    public static final Set<Integer> BUNDLED_REGISTRY_IDS = new HashSet<>();

    private final Map<Integer, JsonObject> registryData = new HashMap<>();
    private final JsonParser parser = new JsonParser();

    static {
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_13);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_13_1);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_14);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_15);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_16);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_16_2);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_17);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_18);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_19);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_19_3);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_19_4);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_20);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_20_3);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MINECRAFT_1_20_5);

        ITEM = new ItemRegistry();
        ENTITY_TYPE = new EntityTypeRegistry();
        ENCHANTMENT = new EnchantmentRegistry();
        DATA_COMPONENT_TYPE = new DataComponentTypeRegistry();
    }

    public static Registries get() {
        if (registries == null)
            registries = new Registries();
        return registries;
    }

    public Registries() {
        loadBundledRegistries();
    }

    private void loadBundledRegistries() {
        BUNDLED_REGISTRY_IDS.forEach(protocolId -> {
            JsonObject data = loadBundledRegistry(protocolId);
            if (data == null) return;
            registryData.put(protocolId, data);
        });
    }

    private JsonObject loadBundledRegistry(int protocolId) {
        String file = getRegistriesFileName(protocolId);
        if (file == null) return null;
        try {
            return parser.parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file))).getAsJsonObject();
        } catch (Throwable ex) {
            return null;
        }
    }

    private String getRegistriesFileName(int protocolId) {
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_13: return "registries_1_13.json";
            case ProtocolConstants.MINECRAFT_1_13_1: return "registries_1_13_1.json";
            case ProtocolConstants.MINECRAFT_1_14: return "registries_1_14.json";
            case ProtocolConstants.MINECRAFT_1_15: return "registries_1_15.json";
            case ProtocolConstants.MINECRAFT_1_16: return "registries_1_16.json";
            case ProtocolConstants.MINECRAFT_1_16_2: return "registries_1_16_2.json";
            case ProtocolConstants.MINECRAFT_1_17: return "registries_1_17.json";
            case ProtocolConstants.MINECRAFT_1_18: return "registries_1_18.json";
            case ProtocolConstants.MINECRAFT_1_19: return "registries_1_19.json";
            case ProtocolConstants.MINECRAFT_1_19_3: return "registries_1_19_3.json";
            case ProtocolConstants.MINECRAFT_1_19_4: return "registries_1_19_4.json";
            case ProtocolConstants.MINECRAFT_1_20: return "registries_1_20.json";
            case ProtocolConstants.MINECRAFT_1_20_3: return "registries_1_20_3.json";
            case ProtocolConstants.MINECRAFT_1_20_5: return "registries_1_20_5.json";
            default: return null;
        }
    }

    public JsonObject getRegistriesData(int protocolId) {
        for (int i = protocolId; i >= 0; i--) {
            if (registryData.containsKey(i))
                return registryData.get(i);
        }
        return null;
    }
}
