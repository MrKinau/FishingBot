package systems.kinau.fishingbot.bot.registry.registries;

import systems.kinau.fishingbot.bot.registry.MetaRegistry;
import systems.kinau.fishingbot.bot.registry.Registry;
import systems.kinau.fishingbot.bot.registry.RegistryLoader;
import systems.kinau.fishingbot.enums.LegacyMaterial;

import java.util.Arrays;
import java.util.Optional;

public class ItemRegistry extends MetaRegistry<Integer, String> {

    public ItemRegistry() {
        Registry<Integer, String> legacyRegistry = new Registry<>();
        Arrays.stream(LegacyMaterial.values()).forEach(legacyMaterial -> {
            legacyRegistry.registerElement(legacyMaterial.getId(), legacyMaterial.name());
        });

        load(RegistryLoader.simple("minecraft:item"), legacyRegistry);
    }

    public String getItemName(int id, int protocol) {
        return Optional.ofNullable(getElement(id, protocol)).orElse("Modded Item");
    }
}
