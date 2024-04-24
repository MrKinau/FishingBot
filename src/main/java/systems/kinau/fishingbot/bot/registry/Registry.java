package systems.kinau.fishingbot.bot.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collection;
import java.util.Set;
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
        return registry.inverse().get(value);
    }
}
