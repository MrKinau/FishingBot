package systems.kinau.fishingbot.utils;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.enums.EnchantmentType;
import systems.kinau.fishingbot.enums.MaterialMc18;
import systems.kinau.fishingbot.modules.fishing.RegistryHandler;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.utils.nbt.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemUtils {

    private static final Map<Integer, Integer> rodId = new HashMap<>();
    private static final Map<Integer, Set<Integer>> fishIds = new HashMap<>();
    private static final Map<Integer, Integer> enchantedBookId = new HashMap<>();

    public static int getRodId(int protocolId) {
        if (rodId.containsKey(protocolId))
            return rodId.get(protocolId);

        if (RegistryHandler.getItemsMap(protocolId).containsValue("minecraft:fishing_rod")) {
            rodId.put(protocolId, RegistryHandler.getItemsMap(protocolId).entrySet().stream()
                    .filter(entry -> entry.getValue().equals("minecraft:fishing_rod"))
                    .findAny().get().getKey());
            return rodId.get(protocolId);
        }
        return 563;
    }

    public static boolean isFishingRod(Slot slot) {
        if (slot == null)
            return false;
        if (!slot.isPresent())
            return false;
        int protocol = ProtocolConstants.getLatest();
        if (FishingBot.getInstance().getCurrentBot() != null)
            protocol = FishingBot.getInstance().getCurrentBot().getServerProtocol();

        if (protocol < ProtocolConstants.MINECRAFT_1_13)
            return MaterialMc18.getMaterial(slot.getItemId()) == MaterialMc18.FISHING_ROD;
        else
            return getRodId(protocol) == slot.getItemId();
    }

    public static boolean isFish(int protocol, int itemId) {
        if (protocol < ProtocolConstants.MINECRAFT_1_13)
            return itemId == MaterialMc18.RAW_FISH.getId();
        if (fishIds.containsKey(protocol))
            return fishIds.get(protocol).contains(itemId);
        Set<Integer> ids = new HashSet<>();
        RegistryHandler.getItemsMap(protocol).forEach((id, name) -> {
            if (name.equals("minecraft:cod") || name.equals("minecraft:salmon") || name.equals("minecraft:tropical_fish") || name.equals("minecraft:pufferfish"))
                ids.add(id);
        });
        fishIds.put(protocol, ids);
        return ids.contains(itemId);
    }

    public static boolean isEnchantedBook(int protocol, int itemId) {
        if (protocol < ProtocolConstants.MINECRAFT_1_13)
            return itemId == MaterialMc18.ENCHANTED_BOOK.getId();
        if (enchantedBookId.containsKey(protocol))
            return enchantedBookId.get(protocol) == itemId;
        AtomicInteger ebId = new AtomicInteger();
        RegistryHandler.getItemsMap(protocol).forEach((id, name) -> {
            if (name.equals("minecraft:enchanted_book"))
                ebId.set(id);
        });
        enchantedBookId.put(protocol, ebId.get());
        return ebId.get() == itemId;
    }

    public static List<Enchantment> getEnchantments(Slot slot) {
        List<Enchantment> enchantmentList = new ArrayList<>();
        Tag<?> rootTag = slot.getNbtData().getTag();
        if (!(rootTag instanceof CompoundTag)) return enchantmentList;
        CompoundTag root = (CompoundTag) rootTag;
        String key;
        if (root.containsKey("StoredEnchantments"))
            key = "StoredEnchantments";
        else if (root.containsKey("ench"))
            key = "ench";
        else if (root.containsKey("Enchantments"))
            key = "Enchantments";
        else
            return enchantmentList;
        List<CompoundTag> enchants = root.get(key, ListTag.class).getValue().stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(tag -> (CompoundTag) tag)
                .collect(Collectors.toList());
        for (CompoundTag enchant : enchants) {
            Optional<EnchantmentType> enchType;
            short level = enchant.get("lvl", ShortTag.class).getValue();
            if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_13) {
                String id = enchant.get("id", StringTag.class).getValue();
                enchType = EnchantmentType.getFromName(id);
                if (!enchType.isPresent()) {
                    enchType = Optional.of(EnchantmentType.FUTURE);
                    enchType.get().setFutureName(id);
                }
            } else {
                short id = enchant.get("id", ShortTag.class).getValue();
                enchType = EnchantmentType.getFromId(Short.valueOf(id).intValue());
                if (!enchType.isPresent()) {
                    enchType = Optional.of(EnchantmentType.FUTURE);
                    enchType.get().setFutureName("ID: " + id);
                }
            }
            enchType.ifPresent(enchantmentType -> enchantmentList.add(new Enchantment(enchantmentType, level)));
        }
        return enchantmentList;
    }

    public static int getDamage(Slot slot) {
        if (slot == null)
            return -1;
        return slot.getItemDamage();
    }

    public static int getFishingRodValue(Slot slot) {
        if (!isFishingRod(slot))
            return -100;
        if (FishingBot.getInstance().getCurrentBot().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(slot) >= 63)
            return -100;
        List<Enchantment> enchantments = getEnchantments(slot);
        short luckOfTheSeaLevel = enchantments.stream()
                .filter(enchantment -> enchantment.getEnchantmentType() == EnchantmentType.LUCK_OF_THE_SEA)
                .map(Enchantment::getLevel).findAny().orElse((short)0);
        short lureLevel = enchantments.stream()
                .filter(enchantment -> enchantment.getEnchantmentType() == EnchantmentType.LURE)
                .map(Enchantment::getLevel).findAny().orElse((short)0);
        short unbreakingLevel = enchantments.stream()
                .filter(enchantment -> enchantment.getEnchantmentType() == EnchantmentType.UNBREAKING)
                .map(Enchantment::getLevel).findAny().orElse((short)0);
        short mendingLevel = enchantments.stream()
                .filter(enchantment -> enchantment.getEnchantmentType() == EnchantmentType.MENDING)
                .map(Enchantment::getLevel).findAny().orElse((short)0);
        short vanishingCurseLevel = enchantments.stream()
                .filter(enchantment -> enchantment.getEnchantmentType() == EnchantmentType.CURSE_OF_VANISHING)
                .map(Enchantment::getLevel).findAny().orElse((short)0);
        return luckOfTheSeaLevel * 9 + lureLevel * 9 + unbreakingLevel * 2 + mendingLevel - vanishingCurseLevel;
    }

    public static int getBestFishingRod(Inventory inventory) {
        AtomicInteger currentBestSlotId = new AtomicInteger(-1);
        AtomicInteger currentBestValue = new AtomicInteger(-100);
        if (inventory == null)
            return -1;
        inventory.getContent().forEach((slotId, slot) -> {
            int currValue = getFishingRodValue(slot);
            if (currValue > currentBestValue.get()) {
                currentBestValue.set(currValue);
                currentBestSlotId.set(slotId);
            }
        });
        return currentBestSlotId.get();
    }

    public static String getItemName(Slot slot) {
        if (FishingBot.getInstance().getCurrentBot() == null || !slot.isPresent())
            return "N/A";
        int version = FishingBot.getInstance().getCurrentBot().getServerProtocol();
        if (version <= ProtocolConstants.MINECRAFT_1_12_2) {
            return MaterialMc18.getMaterialName(slot.getItemId(), Integer.valueOf(slot.getItemDamage()).shortValue());
        } else {
            return RegistryHandler.getItemName(slot.getItemId(), version).replace("minecraft:", "");
        }
    }
}
