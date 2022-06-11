package systems.kinau.fishingbot.utils;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import com.flowpowered.nbt.stream.NBTInputStream;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.enums.EnchantmentType;
import systems.kinau.fishingbot.enums.MaterialMc18;
import systems.kinau.fishingbot.modules.fishing.RegistryHandler;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemUtils {

    private static Map<Integer, Integer> rodId = new HashMap<>();
    private static Map<Integer, Set<Integer>> fishIds = new HashMap<>();
    private static Map<Integer, Integer> enchantedBookId = new HashMap<>();

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
        try {
            NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(slot.getNbtData().clone()), false);
            Tag tag = nbtInputStream.readTag();
            if (tag.getType() == TagType.TAG_COMPOUND) {
                CompoundMap root = (CompoundMap) tag.getValue();
                String key = null;
                if (root.containsKey("StoredEnchantments"))
                    key = "StoredEnchantments";
                else if (root.containsKey("ench"))
                    key = "ench";
                else if (root.containsKey("Enchantments"))
                    key = "Enchantments";
                else
                    return enchantmentList;
                List<CompoundTag> enchants = (List<CompoundTag>) root.get(key).getValue();
                for (CompoundTag enchant : enchants) {
                    Optional<EnchantmentType> enchType;
                    short level = ((Number) enchant.getValue().get("lvl").getValue()).shortValue();
                    if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_13) {
                        String id = (String) enchant.getValue().get("id").getValue();
                        enchType = EnchantmentType.getFromName(id);
                        if (!enchType.isPresent()) {
                            enchType = Optional.of(EnchantmentType.FUTURE);
                            enchType.get().setFutureName(id);
                        }
                    } else {
                        short id = ((Number) enchant.getValue().get("id").getValue()).shortValue();
                        enchType = EnchantmentType.getFromId(Short.valueOf(id).intValue());
                        if (!enchType.isPresent()) {
                            enchType = Optional.of(EnchantmentType.FUTURE);
                            enchType.get().setFutureName("ID: " + id);
                        }
                    }
                    enchType.ifPresent(enchantmentType -> enchantmentList.add(new Enchantment(enchantmentType, level)));
                }
            }
        } catch (IOException ignored) { }
        return enchantmentList;
    }

    public static int getDamage(Slot slot) {
        if (slot == null)
            return -1;
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() < ProtocolConstants.MINECRAFT_1_13)
            return slot.getItemDamage();
        try {
            NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(slot.getNbtData().clone()), false);
            Tag tag = nbtInputStream.readTag();
            if (tag.getType() == TagType.TAG_COMPOUND) {
                CompoundMap root = (CompoundMap) tag.getValue();
                Tag dmgTag = root.get("Damage");
                if (dmgTag.getType() == TagType.TAG_INT)
                    return (int)dmgTag.getValue();
                else if (dmgTag.getType() == TagType.TAG_SHORT)
                    return (short)dmgTag.getValue();
                else
                    return -1;
            }
        } catch (Exception ignored) { }
        return -1;
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
            return MaterialMc18.getMaterialName(slot.getItemId(), slot.getItemDamage());
        } else {
            return RegistryHandler.getItemName(slot.getItemId(), version).replace("minecraft:", "");
        }
    }
}
