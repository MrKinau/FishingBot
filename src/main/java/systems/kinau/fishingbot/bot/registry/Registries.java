package systems.kinau.fishingbot.bot.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.bot.registry.registries.*;
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
    public static ParticleTypeRegistry PARTICLE_TYPE;
    public static ConsumeEffectTypeRegistry CONSUME_EFFECT_TYPE;

    public static final Set<Integer> BUNDLED_REGISTRY_IDS = new HashSet<>();

    private final Map<Integer, JsonObject> registryData = new HashMap<>();
    private final JsonParser parser = new JsonParser();

    static {
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_13);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_13_1);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_14);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_15);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_16);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_16_2);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_17);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_18);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_19);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_19_3);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_19_4);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_20);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_20_3);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_20_5);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_21);
        BUNDLED_REGISTRY_IDS.add(ProtocolConstants.MC_1_21_2);

        ITEM = new ItemRegistry();
        ENTITY_TYPE = new EntityTypeRegistry();
        ENCHANTMENT = new EnchantmentRegistry();
        DATA_COMPONENT_TYPE = new DataComponentTypeRegistry();
        PARTICLE_TYPE = new ParticleTypeRegistry();
        CONSUME_EFFECT_TYPE = new ConsumeEffectTypeRegistry();
    }

    public static Registries get() {
        if (registries == null)
            registries = new Registries();
        return registries;
    }

    public static MetaRegistry<Integer, String> getByIdentifier(String identifier, int protocolId) {
        switch (identifier) {
            case "minecraft:item": return ITEM;
            case "minecraft:entity_type": return ENTITY_TYPE;
            case "minecraft:enchantment": return ENCHANTMENT;
            case "minecraft:data_component_type": return DATA_COMPONENT_TYPE;
            case "minecraft:particle_type": return PARTICLE_TYPE;
            case "minecraft:consume_effect_type": return CONSUME_EFFECT_TYPE;
        }
        return null;
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
            case ProtocolConstants.MC_1_13: return "mc_data/1_13/registries.json";
            case ProtocolConstants.MC_1_13_1: return "mc_data/1_13_1/registries.json";
            case ProtocolConstants.MC_1_14: return "mc_data/1_14/registries.json";
            case ProtocolConstants.MC_1_15: return "mc_data/1_15/registries.json";
            case ProtocolConstants.MC_1_16: return "mc_data/1_16/registries.json";
            case ProtocolConstants.MC_1_16_2: return "mc_data/1_16_2/registries.json";
            case ProtocolConstants.MC_1_17: return "mc_data/1_17/registries.json";
            case ProtocolConstants.MC_1_18: return "mc_data/1_18/registries.json";
            case ProtocolConstants.MC_1_19: return "mc_data/1_19/registries.json";
            case ProtocolConstants.MC_1_19_3: return "mc_data/1_19_3/registries.json";
            case ProtocolConstants.MC_1_19_4: return "mc_data/1_19_4/registries.json";
            case ProtocolConstants.MC_1_20: return "mc_data/1_20/registries.json";
            case ProtocolConstants.MC_1_20_3: return "mc_data/1_20_3/registries.json";
            case ProtocolConstants.MC_1_20_5: return "mc_data/1_20_5/registries.json";
            case ProtocolConstants.MC_1_21: return "mc_data/1_21/registries.json";
            case ProtocolConstants.MC_1_21_2: return "mc_data/1_21_2/registries.json";
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
