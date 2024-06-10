package systems.kinau.fishingbot.bot.registry;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public interface RegistryLoader<S, K, V> extends Function<S, Registry<K, V>> {

    interface Json<K, V> extends RegistryLoader<JsonObject, K, V> { }

    interface Map<K, V> extends RegistryLoader<java.util.Map<String, ?>, K, V> { }

    Comparator<Object> RESOURCE_LOCATION_COMPARATOR = (s, s1) -> {
        if (s instanceof String && s1 instanceof String) {
            String[] resourceLocation = ((String)s).split(":");
            String[] resourceLocation1 = ((String)s1).split(":");
            String toCompare = resourceLocation[resourceLocation.length - 1];
            String toCompare1 = resourceLocation1[resourceLocation1.length - 1];
            return toCompare.compareTo(toCompare1);
        }
        return 0;
    };

    static Json<Integer, String> simple(String key) {
        return root -> {
            if (!root.getAsJsonObject().has(key))
                return null;
            JsonObject entries = root.getAsJsonObject(key).getAsJsonObject("entries");
            Registry<Integer, String> registry = new Registry<>();
            entries.keySet().forEach(entry -> {
                if (entries.get(entry).isJsonObject() && entries.get(entry).getAsJsonObject().has("protocol_id")) {
                    registry.registerElement(entries.get(entry).getAsJsonObject().getAsJsonPrimitive("protocol_id").getAsInt(), entry);
                }
            });
            return registry;
        };
    }

    static Map<Integer, String> mapped() {
        return stringMap -> {
            List<String> keys = new ArrayList<>(stringMap.keySet());
            keys.sort(RESOURCE_LOCATION_COMPARATOR);
            Registry<Integer, String> registry = new Registry<>();
            int i = 0;
            for (String identifier : keys) {
                registry.registerElement(i++, identifier);
            }
            return registry;
        };
    }
}
