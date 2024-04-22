package systems.kinau.fishingbot.bot.registry.registries;

import systems.kinau.fishingbot.bot.registry.MetaRegistry;
import systems.kinau.fishingbot.bot.registry.RegistryLoader;

public class DataComponentTypeRegistry extends MetaRegistry<Integer, String> {

    public DataComponentTypeRegistry() {
        load(RegistryLoader.simple("minecraft:data_component_type"));
    }

}
