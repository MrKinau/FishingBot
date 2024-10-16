package systems.kinau.fishingbot.bot.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import systems.kinau.fishingbot.FishingBot;

import java.util.*;
import java.util.function.BiConsumer;

public class Registry<K, V> {

    private final BiMap<K, V> registry = HashBiMap.create();

    public V getElement(K key) {
        return registry.get(key);
    }

    public void reset() {
        registry.clear();
    }

    public void registerElement(K key, V value) {
        if (registry.containsValue(value)) {
            BiMap<V, K> inverted = registry.inverse();
            inverted.remove(value);
        }
        registry.put(key, value);
    }

    public Set<K> keySet() {
        return registry.keySet();
    }

    public Collection<V> values() {
        return registry.values();
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        registry.forEach(action);
    }

    public boolean containsValue(V value) {
        return registry.containsValue(value);
    }

    public K findKey(V value) {
        K key = registry.inverse().get(value);
        if (key == null)
            FishingBot.getLog().severe("Could not find key for value: " + value);
        return key;
    }

    public void merge(Registry<Integer, String> other) {
        Set<String> identifiers = (Set<String>) new HashSet<>(values());
        identifiers.addAll(other.values());

        List<String> identifierList = new ArrayList<>(identifiers);
        identifierList.sort(RegistryLoader.RESOURCE_LOCATION_COMPARATOR);
        Integer i = 0;
        reset();
        for (String identifier : identifierList) {
            registerElement((K)i++, (V)identifier);
        }
    }
}
