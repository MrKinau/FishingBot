package systems.kinau.fishingbot.bot.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.registries.CommandArgumentTypeRegistry;
import systems.kinau.fishingbot.bot.registry.registries.ConsumeEffectTypeRegistry;
import systems.kinau.fishingbot.bot.registry.registries.DataComponentTypeRegistry;
import systems.kinau.fishingbot.bot.registry.registries.EnchantmentRegistry;
import systems.kinau.fishingbot.bot.registry.registries.EntityTypeRegistry;
import systems.kinau.fishingbot.bot.registry.registries.ItemRegistry;
import systems.kinau.fishingbot.bot.registry.registries.ParticleTypeRegistry;
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
    public static CommandArgumentTypeRegistry COMMAND_ARGUMENT_TYPE;

    public static final Set<Integer> BUNDLED_REGISTRIES = new HashSet<>();

    private final Map<Integer, JsonObject> registryData = new HashMap<>();
    private final JsonParser parser = new JsonParser();

    static {
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_13);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_13_1);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_14);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_15);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_16);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_16_2);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_17);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_18);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_19);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_19_3);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_19_4);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_20);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_20_3);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_20_5);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_2);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_4);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_5);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_6);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_7);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_9);
        BUNDLED_REGISTRIES.add(ProtocolConstants.MC_1_21_11);

        ITEM = new ItemRegistry();
        ENTITY_TYPE = new EntityTypeRegistry();
        ENCHANTMENT = new EnchantmentRegistry();
        DATA_COMPONENT_TYPE = new DataComponentTypeRegistry();
        PARTICLE_TYPE = new ParticleTypeRegistry();
        CONSUME_EFFECT_TYPE = new ConsumeEffectTypeRegistry();
        COMMAND_ARGUMENT_TYPE = new CommandArgumentTypeRegistry();
    }

    public static String getRegistryLocation(int protocolId) {
        return getRegistryLocation(protocolId, "registries.json");
    }

    public static String getPacketRegistryLocation(int protocolId) {
        return getRegistryLocation(protocolId, "packets.json");
    }

    private static String getRegistryLocation(int protocolId, String fileName) {
        if (protocolId == ProtocolConstants.AUTOMATIC)
            protocolId = ProtocolConstants.getLatest();
        String version = ProtocolConstants.getFirstVersionStringByPVN(protocolId);
        if (protocolId == ProtocolConstants.MC_1_21_7)
            version = ProtocolConstants.getVersionString(ProtocolConstants.MC_1_21_6);
        if (version.contains("-"))
            version = version.split("-")[0];
        version = version.replace(".", "_").trim();
        return "mc_data/" + version + "/" + fileName;
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
        BUNDLED_REGISTRIES.forEach(protocolId -> {
            JsonObject data = loadBundledRegistry(protocolId);
            if (data == null) return;
            registryData.put(protocolId, data);
        });
    }

    private JsonObject loadBundledRegistry(int protocolId) {
        String file = getRegistryLocation(protocolId);
        try {
            return parser.parse(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file))).getAsJsonObject();
        } catch (Throwable ex) {
            FishingBot.getLog().severe("Could not load registry file for pvn " + protocolId);
            return null;
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
