package systems.kinau.fishingbot.bot.registry;

import com.google.gson.JsonObject;

import java.util.function.Function;

public interface RegistryLoader<S, K, V> extends Function<S, Registry<K, V>> {

    interface Json<K, V> extends RegistryLoader<JsonObject, K, V> { }

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

}
