package systems.kinau.fishingbot.network.item.datacomponent;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.network.item.datacomponent.components.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DataComponentRegistry {

    private final Map<Integer, Function<Integer, DataComponent>> registry = new HashMap<>();

    public DataComponentRegistry() {
        registry.put(0, NBTComponent::new); // Custom Data only Compound tag
        registry.put(1, VarIntComponent::new);
        registry.put(2, VarIntComponent::new);
        registry.put(3, DamageComponent::new);
        registry.put(4, BooleanComponent::new);
        registry.put(5, NBTComponent::new); // Custom Data only Compound tag
        registry.put(6, NBTComponent::new); // Custom Data only Compound tag
        registry.put(7, LoreComponent::new);
        registry.put(8, VarIntComponent::new);
        registry.put(9, EnchantmentsComponent::new);
        registry.put(10, AdventureModeComponent::new);
        registry.put(11, AdventureModeComponent::new);
        registry.put(12, AttributeModifiersComponent::new);
        registry.put(13, VarIntComponent::new);
        registry.put(14, EmptyComponent::new);
        registry.put(15, EmptyComponent::new);
        registry.put(16, VarIntComponent::new);
        registry.put(17, EmptyComponent::new);
        registry.put(18, BooleanComponent::new);
        registry.put(19, NBTComponent::new); // Custom Data only Compound tag
        registry.put(20, FoodComponent::new);
        registry.put(21, EmptyComponent::new);
        registry.put(22, ToolComponent::new);
        registry.put(23, EnchantmentsComponent::new);
        registry.put(24, DyedColorComponent::new);
        registry.put(25, IntComponent::new);
        registry.put(26, VarIntComponent::new);
        registry.put(27, NBTComponent::new);
        registry.put(28, VarIntComponent::new);
        registry.put(29, componentTypeId -> new ItemListComponent(this, componentTypeId));
        registry.put(30, componentTypeId -> new ItemListComponent(this, componentTypeId));
        registry.put(31, PotionContentsComponent::new); // Custom Data only Compound tag
        registry.put(32, SuspiciousStewEffectsComponent::new);
        registry.put(33, WritableBookContentComponent::new);
        registry.put(34, WrittenBookContentComponent::new);
        registry.put(35, TrimComponent::new);
        registry.put(36, NBTComponent::new); // Custom Data only Compound tag
        registry.put(37, NBTComponent::new); // Custom Data only Compound tag
        registry.put(38, NBTComponent::new); // Custom Data only Compound tag
        registry.put(39, NBTComponent::new); // Custom Data only Compound tag
        registry.put(40, VarIntComponent::new);
        registry.put(41, VarIntComponent::new);
        registry.put(42, NBTComponent::new); // Custom Data only Compound tag
        registry.put(43, LodestoneTrackerComponent::new);
        registry.put(44, componentTypeId -> new SimpleMapperComponent(new FireworksComponent.FireworkExplosion(), componentTypeId));
        registry.put(45, FireworksComponent::new);
        registry.put(46, ProfileComponent::new);
        registry.put(47, StringComponent::new);
        registry.put(48, BannerPatternsComponent::new);
        registry.put(49, VarIntComponent::new);
        registry.put(50, VarIntListComponent::new);
        registry.put(51, componentTypeId -> new ItemListComponent(this, componentTypeId));
        registry.put(52, BlockStateComponent::new);
        registry.put(53, BeesComponent::new);
        registry.put(54, NBTComponent::new); // Custom Data only Compound tag
        registry.put(55, NBTComponent::new); // Custom Data only Compound tag

        Registries.DATA_COMPONENT_TYPE.getRegistry(FishingBot.getInstance().getCurrentBot().getServerProtocol()).forEach((id, name) -> {
            if (!registry.containsKey(id))
                FishingBot.getLog().severe("Unhandled data component " + id + " " + name);
        });
    }

    public DataComponent createComponent(int id, int protocolId) {
        return Optional.ofNullable(registry.get(id)).map(constructor -> constructor.apply(id)).orElse(null);
    }
}
