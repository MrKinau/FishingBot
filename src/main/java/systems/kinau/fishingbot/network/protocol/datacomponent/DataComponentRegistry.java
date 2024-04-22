package systems.kinau.fishingbot.network.protocol.datacomponent;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.protocol.datacomponent.components.DamageComponent;
import systems.kinau.fishingbot.network.protocol.datacomponent.components.EnchantmentsComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DataComponentRegistry {

    private final Map<Integer, Function<Integer, DataComponent>> registry = new HashMap<>();

    public DataComponentRegistry() {
        registry.put(3, DamageComponent::new);
        registry.put(9, EnchantmentsComponent::new);
        registry.put(23, EnchantmentsComponent::new);

        Registries.DATA_COMPONENT_TYPE.getRegistry(FishingBot.getInstance().getCurrentBot().getServerProtocol()).forEach((id, name) -> {
            if (!registry.containsKey(id))
                FishingBot.getLog().severe("Unhandled data component " + id + " " + name);
        });
    }

    public DataComponent createComponent(int id, int protocolId) {
        return Optional.ofNullable(registry.get(id)).map(constructor -> constructor.apply(id)).orElse(null);
    }
}
