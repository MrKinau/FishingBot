package systems.kinau.fishingbot.bot.registry.registries;

import systems.kinau.fishingbot.bot.registry.MetaRegistry;
import systems.kinau.fishingbot.bot.registry.Registry;
import systems.kinau.fishingbot.bot.registry.RegistryLoader;
import systems.kinau.fishingbot.enums.LegacyEnchantmentType;

import java.util.Optional;

public class EnchantmentRegistry extends MetaRegistry<Integer, String> {

    public EnchantmentRegistry() {
        Registry<Integer, String> legacyRegistry = new Registry<>();
        for (LegacyEnchantmentType legacyEnchantmentType : LegacyEnchantmentType.values()) {
            String name = "minecraft:" + legacyEnchantmentType.getName();
            legacyRegistry.registerElement(legacyEnchantmentType.getLegacyId(), name);
        }

        load(RegistryLoader.simple("minecraft:enchantment"), legacyRegistry);
    }

    public String getEnchantmentName(int id, int protocol) {
        return Optional.ofNullable(getElement(id, protocol)).orElse("Unknown Enchantment");
    }
}
