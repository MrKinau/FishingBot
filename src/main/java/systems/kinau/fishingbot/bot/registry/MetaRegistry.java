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
        int nearestRegistry = -1;
        for (Integer registryId : versionToRegistry.keySet()) {
            if (registryId > nearestRegistry && protocolId >= registryId) {
                nearestRegistry = registryId;
            }
        }
        if (nearestRegistry == -1) return null;
        return versionToRegistry.get(nearestRegistry);
    }

    protected void addRegistry(int protocolId, Registry<K, V> registry) {
        if (versionToRegistry.containsKey(protocolId))
            versionToRegistry.get(protocolId).merge((Registry<Integer, String>) registry);
        else
            versionToRegistry.put(protocolId, registry);
    }

    protected void load(RegistryLoader.Json<K, V> registryLoader) {
        load(registryLoader, null);
    }

    protected void load(RegistryLoader.Json<K, V> registryLoader, Registry<K, V> legacyRegistry) {
        Registries.BUNDLED_REGISTRIES.forEach(protocolId -> {
            JsonObject registriesData = Registries.get().getRegistriesData(protocolId);
            Registry<K, V> registry = registryLoader.apply(registriesData);
            if (registry == null) return;
            addRegistry(protocolId, registry);
        });
        if (legacyRegistry != null)
            addRegistry(ProtocolConstants.MC_1_8, legacyRegistry);
    }

    public void load(Map<String, ?> networkRegistry, RegistryLoader.Map<K, V> registryLoader, int protocolId) {
        Registry<K, V> registry = registryLoader.apply(networkRegistry);
        if (registry == null) return;
        addRegistry(protocolId, registry);
    }
}
