package systems.kinau.fishingbot.network.item.datacomponent;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.bot.registry.Registry;
import systems.kinau.fishingbot.network.item.datacomponent.components.AdventureModeComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.AttributeModifiersComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.BannerPatternsComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.BeesComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.BlockStateComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.BlocksAttacksComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.BooleanComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.ConsumableComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.CustomModelDataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.DamageComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.DeathProtectionComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.DyedItemColorComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.EmptyComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.EnchantmentsComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.EquippableComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.FireworksComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.FloatComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.FoodComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.HolderSetComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.InstrumentComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.IntComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.ItemListComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.JukeboxPlayableComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.LodestoneTrackerComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.LoreComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.NBTComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.PaintingVariantComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.PotionContentsComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.ProfileComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.ProvidesTrimMaterialComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.SimpleMapperComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.SoundEventComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.StringComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.SuspiciousStewEffectsComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.ToolComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.TooltipDisplayComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.TrimComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.TypedEntityDataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.UseCooldownComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.UseRemainderComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.VarIntComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.VarIntListComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.WeaponComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.WritableBookContentComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.WrittenBookContentComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.parts.fireworks.FireworkExplosion;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DataComponentRegistry {

    private final Map<Integer, Function<Integer, DataComponent>> registry = new HashMap<>();

    public DataComponentRegistry() {
        Registry<Integer, String> dataComponentRegistry = Registries.DATA_COMPONENT_TYPE.getRegistry(FishingBot.getInstance().getCurrentBot().getServerProtocol());
        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();
        addToRegistry(dataComponentRegistry.findKey("minecraft:custom_data"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:max_stack_size"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:max_damage"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:damage"), DamageComponent::new);
        if (protocolId < ProtocolConstants.MC_1_21_5) {
            addToRegistry(dataComponentRegistry.findKey("minecraft:unbreakable"), BooleanComponent::new);
        } else {
            addToRegistry(dataComponentRegistry.findKey("minecraft:unbreakable"), EmptyComponent::new);
        }
        addToRegistry(dataComponentRegistry.findKey("minecraft:custom_name"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:item_name"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:lore"), LoreComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:rarity"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:enchantments"), EnchantmentsComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:can_place_on"), AdventureModeComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:can_break"), AdventureModeComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:attribute_modifiers"), AttributeModifiersComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:custom_model_data"), CustomModelDataComponent::new);
        if (protocolId < ProtocolConstants.MC_1_21_5) {
            addToRegistry(dataComponentRegistry.findKey("minecraft:hide_additional_tooltip"), EmptyComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:hide_tooltip"), EmptyComponent::new);
        }
        addToRegistry(dataComponentRegistry.findKey("minecraft:repair_cost"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:creative_slot_lock"), EmptyComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:enchantment_glint_override"), BooleanComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:intangible_projectile"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:food"), componentTypeId -> new FoodComponent(this, componentTypeId));
        addToRegistry(dataComponentRegistry.findKey("minecraft:tool"), ToolComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:stored_enchantments"), EnchantmentsComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:dyed_color"), DyedItemColorComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:map_color"), IntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:map_id"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:map_decorations"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:map_post_processing"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:charged_projectiles"), componentTypeId -> new ItemListComponent(this, componentTypeId));
        addToRegistry(dataComponentRegistry.findKey("minecraft:bundle_contents"), componentTypeId -> new ItemListComponent(this, componentTypeId));
        addToRegistry(dataComponentRegistry.findKey("minecraft:potion_contents"), PotionContentsComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:suspicious_stew_effects"), SuspiciousStewEffectsComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:writable_book_content"), WritableBookContentComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:written_book_content"), WrittenBookContentComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:trim"), TrimComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:debug_stick_state"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:entity_data"), TypedEntityDataComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:bucket_entity_data"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:block_entity_data"), TypedEntityDataComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:instrument"), InstrumentComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:ominous_bottle_amplifier"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:recipes"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:lodestone_tracker"), LodestoneTrackerComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:firework_explosion"), componentTypeId -> new SimpleMapperComponent(new FireworkExplosion(), componentTypeId));
        addToRegistry(dataComponentRegistry.findKey("minecraft:fireworks"), FireworksComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:profile"), ProfileComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:note_block_sound"), StringComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:banner_patterns"), BannerPatternsComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:base_color"), VarIntComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:pot_decorations"), VarIntListComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:container"), componentTypeId -> new ItemListComponent(this, componentTypeId));
        addToRegistry(dataComponentRegistry.findKey("minecraft:block_state"), BlockStateComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:bees"), BeesComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:lock"), NBTComponent::new);
        addToRegistry(dataComponentRegistry.findKey("minecraft:container_loot"), NBTComponent::new);

        if (protocolId >= ProtocolConstants.MC_1_21) {
            addToRegistry(dataComponentRegistry.findKey("minecraft:jukebox_playable"), JukeboxPlayableComponent::new);
        }

        if (protocolId <= ProtocolConstants.MC_1_21) {
            addToRegistry(dataComponentRegistry.findKey("minecraft:fire_resistant"), EmptyComponent::new);
        }

        if (protocolId >= ProtocolConstants.MC_1_21_2) {
            addToRegistry(dataComponentRegistry.findKey("minecraft:item_model"), StringComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:consumable"), ConsumableComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:use_remainder"), componentTypeId -> new UseRemainderComponent(this, componentTypeId));
            addToRegistry(dataComponentRegistry.findKey("minecraft:use_cooldown"), UseCooldownComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:damage_resistant"), StringComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:enchantable"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:equippable"), EquippableComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:repairable"), HolderSetComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:glider"), EmptyComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:tooltip_style"), StringComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:death_protection"), DeathProtectionComponent::new);
        }

        if (protocolId >= ProtocolConstants.MC_1_21_5) {
            addToRegistry(dataComponentRegistry.findKey("minecraft:tooltip_display"), TooltipDisplayComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:weapon"), WeaponComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:blocks_attacks"), BlocksAttacksComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:potion_duration_scale"), FloatComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:provides_trim_material"), ProvidesTrimMaterialComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:provides_banner_patterns"), StringComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:break_sound"), SoundEventComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:villager/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:wolf/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:wolf/sound_variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:wolf/collar"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:fox/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:salmon/size"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:parrot/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:tropical_fish/pattern"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:tropical_fish/base_color"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:tropical_fish/pattern_color"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:mooshroom/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:rabbit/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:pig/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:cow/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:chicken/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:frog/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:horse/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:painting/variant"), PaintingVariantComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:llama/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:axolotl/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:cat/variant"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:cat/collar"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:sheep/color"), VarIntComponent::new);
            addToRegistry(dataComponentRegistry.findKey("minecraft:shulker/color"), VarIntComponent::new);
        }

        dataComponentRegistry.forEach((id, name) -> {
            if (!registry.containsKey(id))
                FishingBot.getLog().severe("Unhandled data component " + id + " " + name);
        });
    }

    private void addToRegistry(Integer id, Function<Integer, DataComponent> typeToComponentFunction) {
        if (id == null) {
            FishingBot.getLog().severe("Tried to add null to data component registry");
            return;
        }
        registry.put(id, typeToComponentFunction);
    }

    public DataComponent createComponent(int id, int protocolId) {
        return Optional.ofNullable(registry.get(id)).map(constructor -> constructor.apply(id)).orElse(null);
    }
}
