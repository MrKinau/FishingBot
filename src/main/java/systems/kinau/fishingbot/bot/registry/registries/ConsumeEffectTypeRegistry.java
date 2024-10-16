package systems.kinau.fishingbot.bot.registry.registries;

import systems.kinau.fishingbot.bot.registry.MetaRegistry;
import systems.kinau.fishingbot.bot.registry.RegistryLoader;

import java.util.Optional;

public class ConsumeEffectTypeRegistry extends MetaRegistry<Integer, String> {

    public ConsumeEffectTypeRegistry() {
        load(RegistryLoader.simple("minecraft:consume_effect_type"));
    }

    public int getConsumeEffectTypeId(String consumeEffectTypeName, int protocol) {
        return Optional.ofNullable(findKey(consumeEffectTypeName, protocol)).orElse(0);
    }

    public String getConsumeEffectTypeName(int consumeEffectTypeId, int protocol) {
        return getElement(consumeEffectTypeId, protocol);
    }
}
