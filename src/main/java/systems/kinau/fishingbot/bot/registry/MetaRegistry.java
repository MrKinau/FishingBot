package systems.kinau.fishingbot.bot.registry;

import com.google.gson.JsonObject;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;

public class MetaRegistry<K, V> {

    private final Map<Integer, Registry<K, V>> versionToRegistry = new HashMap<>();

    public V getElement(K key, int protocolId) {
        Registry<K, V> registry = getRegistry(protocolId);
        if (registry == null) return null;
        return registry.getElement(key);
    }

    public K findKey(V value, int protocolId) {
        Registry<K, V> registry = getRegistry(protocolId);
        if (registry == null) return null;
        return registry.findKey(value);
    }

    public Registry<K, V> getRegistry(int protocolId) {
        for (int i = protocolId; i >= 0; i--) {
            if (versionToRegistry.containsKey(i))
                return versionToRegistry.get(i);
        }
        return null;
    }

    protected void addRegistry(int protocolId, Registry<K, V> registry) {
        versionToRegistry.put(protocolId, registry);
    }

    protected void load(RegistryLoader.Json<K, V> registryLoader) {
        load(registryLoader, null);
    }

    protected void load(RegistryLoader.Json<K, V> registryLoader, Registry<K, V> legacyRegistry) {
        Registries.BUNDLED_REGISTRY_IDS.forEach(protocolId -> {
            JsonObject registriesData = Registries.get().getRegistriesData(protocolId);
            Registry<K, V> registry = registryLoader.apply(registriesData);
            if (registry == null) return;
            addRegistry(protocolId, registry);
        });
        if (legacyRegistry != null)
            addRegistry(ProtocolConstants.MINECRAFT_1_8, legacyRegistry);
    }
}
