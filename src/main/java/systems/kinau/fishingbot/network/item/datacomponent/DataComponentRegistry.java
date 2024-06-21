package systems.kinau.fishingbot.network.item.datacomponent;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.bot.registry.Registry;
import systems.kinau.fishingbot.network.item.datacomponent.components.*;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DataComponentRegistry {

    private final Map<Integer, Function<Integer, DataComponent>> registry = new HashMap<>();

    public DataComponentRegistry() {
        Registry<Integer, String> dataComponentRegistry = Registries.DATA_COMPONENT_TYPE.getRegistry(FishingBot.getInstance().getCurrentBot().getServerProtocol());
        registry.put(dataComponentRegistry.findKey("minecraft:custom_data"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:max_stack_size"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:max_damage"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:damage"), DamageComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:unbreakable"), BooleanComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:custom_name"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:item_name"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:lore"), LoreComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:rarity"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:enchantments"), EnchantmentsComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:can_place_on"), AdventureModeComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:can_break"), AdventureModeComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:attribute_modifiers"), AttributeModifiersComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:custom_model_data"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:hide_additional_tooltip"), EmptyComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:hide_tooltip"), EmptyComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:repair_cost"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:creative_slot_lock"), EmptyComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:enchantment_glint_override"), BooleanComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:intangible_projectile"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:food"), componentTypeId -> new FoodComponent(this, componentTypeId));
        registry.put(dataComponentRegistry.findKey("minecraft:fire_resistant"), EmptyComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:tool"), ToolComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:stored_enchantments"), EnchantmentsComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:dyed_color"), DyedColorComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:map_color"), IntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:map_id"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:map_decorations"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:map_post_processing"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:charged_projectiles"), componentTypeId -> new ItemListComponent(this, componentTypeId));
        registry.put(dataComponentRegistry.findKey("minecraft:bundle_contents"), componentTypeId -> new ItemListComponent(this, componentTypeId));
        registry.put(dataComponentRegistry.findKey("minecraft:potion_contents"), PotionContentsComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:suspicious_stew_effects"), SuspiciousStewEffectsComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:writable_book_content"), WritableBookContentComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:written_book_content"), WrittenBookContentComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:trim"), TrimComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:debug_stick_state"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:entity_data"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:bucket_entity_data"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:block_entity_data"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:instrument"), InstrumentComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:ominous_bottle_amplifier"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:recipes"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:lodestone_tracker"), LodestoneTrackerComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:firework_explosion"), componentTypeId -> new SimpleMapperComponent(new FireworksComponent.FireworkExplosion(), componentTypeId));
        registry.put(dataComponentRegistry.findKey("minecraft:fireworks"), FireworksComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:profile"), ProfileComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:note_block_sound"), StringComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:banner_patterns"), BannerPatternsComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:base_color"), VarIntComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:pot_decorations"), VarIntListComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:container"), componentTypeId -> new ItemListComponent(this, componentTypeId));
        registry.put(dataComponentRegistry.findKey("minecraft:block_state"), BlockStateComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:bees"), BeesComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:lock"), NBTComponent::new);
        registry.put(dataComponentRegistry.findKey("minecraft:container_loot"), NBTComponent::new);

        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_21) {
            registry.put(dataComponentRegistry.findKey("minecraft:jukebox_playable"), JukeboxPlayableComponent::new);
        }

        dataComponentRegistry.forEach((id, name) -> {
            if (!registry.containsKey(id))
                FishingBot.getLog().severe("Unhandled data component " + id + " " + name);
        });
    }

    public DataComponent createComponent(int id, int protocolId) {
        return Optional.ofNullable(registry.get(id)).map(constructor -> constructor.apply(id)).orElse(null);
    }
}
