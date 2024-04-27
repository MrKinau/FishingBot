/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules.fishing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.bot.loot.LootHistory;
import systems.kinau.fishingbot.bot.loot.LootItem;
import systems.kinau.fishingbot.bot.registry.Registries;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.modules.Module;
import systems.kinau.fishingbot.network.entity.EntityDataValue;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChatMessage;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseItem;
import systems.kinau.fishingbot.utils.ItemUtils;
import systems.kinau.fishingbot.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class FishingModule extends Module implements Runnable, Listener {

    @Getter(AccessLevel.NONE)
    private int BOBBER_ENTITY_TYPE;
    @Getter(AccessLevel.NONE)
    private int ITEM_ENTITY_TYPE;

    private final PossibleCaughtList possibleCaughtItems = new PossibleCaughtList();

    private Bobber currentBobber;
    private short lastY = -1;
    private boolean trackingNextBobberId = false;
    private boolean noRodAvailable = false;
    private boolean paused = false;
    private boolean trackingNextEntityMeta = false;
    private long lastFish = System.currentTimeMillis();

    private int currentFishingRodValue;

    private Thread stuckingFix;
    private boolean joined;
    private LootHistory lootHistory;

    public FishingModule(LootHistory savedLootHistory) {
        this.lootHistory = savedLootHistory;
        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();

        BOBBER_ENTITY_TYPE = Registries.ENTITY_TYPE.getEntityType("minecraft:fishing_bobber", protocolId);
        if (BOBBER_ENTITY_TYPE == 0)
            BOBBER_ENTITY_TYPE = 90;

        ITEM_ENTITY_TYPE = Registries.ENTITY_TYPE.getEntityType("minecraft:item", protocolId);
        if (ITEM_ENTITY_TYPE == 0)
            ITEM_ENTITY_TYPE = 2;
    }

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        if (FishingBot.getInstance().getCurrentBot().getConfig().isStuckingFixEnabled()) {
            stuckingFix = new Thread(this);
            stuckingFix.setName("stuckingFixThread");
            stuckingFix.start();
        }
    }

    @Override
    public void onDisable() {
        if (stuckingFix != null)
            stuckingFix.interrupt();
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    public void stuck() {
        if (isPaused()) return;
        if (isNoRodAvailable()) return;
        if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) return;
        setLastFish(System.currentTimeMillis());
        setTrackingNextBobberId(true);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignore) {
        }
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            if (getCurrentBobber() != null && !isTrackingNextEntityMeta())
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
        } else {
            stuck();
        }
    }

    public void fish() {
        setLastFish(System.currentTimeMillis());
        Bobber bobberUsedForCatch = getCurrentBobber();
        setCurrentBobber(null);
        setTrackingNextEntityMeta(true);
        if (isPaused())
            return;
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
        new Thread(() -> {
            try {
                int timeToWait = FishingBot.getInstance().getCurrentBot().getPlayer().getLastPing() + 200;
                Thread.sleep(timeToWait);
                setTrackingNextEntityMeta(false);
                getCaughtItem(bobberUsedForCatch);
                Thread.sleep(200);
                if (FishingBot.getInstance().getCurrentBot().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem()) >= 63) {
                    noRod();
                    return;
                }
                if (isPaused())
                    return;
                setTrackingNextBobberId(true);
                Thread.sleep(200);

                if (FishingBot.getInstance().getCurrentBot().getPlayer() != null
                        && !FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) {
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
                } else {
                    setTrackingNextBobberId(false);
                }
            } catch (InterruptedException ignore) {
            }
        }).start();
    }

    private void getCaughtItem(Bobber bobberUsedForCatch) {
        if (getPossibleCaughtItems().isEmpty())
            return;
        // Scores each new item based of several factors to how likely it is the caught item
        // Current factors are:
        // - Maximum velocity (usually the caught items start with a very high velocity)
        // - Item Origin location distance to bobber location (items origin located near the bobber ranked significantly higher)
        Map<Item, Double> itemScore = getPossibleCaughtItems().stream().collect(Collectors.toMap(Function.identity(), item -> Double.valueOf(item.getMaxMot())));

        itemScore.entrySet().forEach(entry -> {
            Item item = entry.getKey();
            double score = Optional.ofNullable(entry.getValue()).orElse(0.0);
            double distToBobber = Math.abs(Math.sqrt(Math.pow(bobberUsedForCatch.getCurrentX() - item.getOriginX(), 2) + Math.pow(bobberUsedForCatch.getCurrentY() - item.getOriginY(), 2) + Math.pow(bobberUsedForCatch.getCurrentZ() - item.getOriginZ(), 2)));
            if (distToBobber < 1)
                score += 20_000;
            else {
                score /= distToBobber;
            }
            entry.setValue(score);
        });

        // Clear mem
        getPossibleCaughtItems().clear();

        Item currentMax = itemScore.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        // Print to console (based on announcetype)
        logItem(currentMax,
                FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeConsole(),
                FishingBot.getLog()::info,
                FishingBot.getLog()::info);

        // Print in mc chat (based on announcetype)
        logItem(currentMax,
                FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeChat(),
                (String str) -> FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(FishingBot.PREFIX + str)),
                (String str) -> {
                    // Delay the enchant messages to arrive after the item announcement
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignore) {
                    }
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(str));
                });

        LootItem lootItem = getLootHistory().registerItem(currentMax.getName(), currentMax.getDisplayName(), currentMax.getEnchantments());

        if (currentMax.getEnchantments() == null)
            currentMax.setEnchantments(new ArrayList<>());
        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new FishCaughtEvent(currentMax, lootItem));
    }


    private String stringify(Item item) {
        return FishingBot.getI18n().t("module-fishing-caught-item", item.getDisplayName());
    }

    public void logItem(Item item, AnnounceType noisiness, Consumer<String> announce, Consumer<String> announceEnchants) {
        if (noisiness == AnnounceType.NONE)
            return;
        else if (noisiness == AnnounceType.ALL)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ALL_BUT_FISH && !ItemUtils.isFish(FishingBot.getInstance().getCurrentBot().getServerProtocol(), item.getItemId()))
            announce.accept(stringify(item));

        if (item.getEnchantments() == null || item.getEnchantments().isEmpty())
            return;

        if (noisiness == AnnounceType.ONLY_ENCHANTED)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ONLY_BOOKS && ItemUtils.isEnchantedBook(FishingBot.getInstance().getCurrentBot().getServerProtocol(), item.getItemId()))
            announce.accept(stringify(item));
        if (noisiness == AnnounceType.ONLY_BOOKS && !ItemUtils.isEnchantedBook(FishingBot.getInstance().getCurrentBot().getServerProtocol(), item.getItemId()))
            return;

        if (!item.getEnchantments().isEmpty()) {
            for (Enchantment enchantment : item.getEnchantments()) {
                String asText = "-> "
                        + enchantment.getDisplayName()
                        + (enchantment.getLevel() > 1 ? " " + StringUtils.getRomanLevel(enchantment.getLevel()) : "");
                announceEnchants.accept(asText);
            }
        }
    }

    private void noRod() {
        if (FishingBot.getInstance().getCurrentBot().getConfig().isDisableRodChecking())
            return;
        boolean swapped = swapWithBestFishingRod();
        if (!swapped) {
            this.noRodAvailable = true;

            FishingBot.getI18n().warning("module-fishing-no-rod-available");
        }
    }

    private void reFish(int id, double x, double y, double z) {
        setTrackingNextBobberId(false);
        setCurrentBobber(new Bobber(id, x, y, z));
    }

    public boolean swapWithBestFishingRod() {
        int bestSlot = ItemUtils.getBestFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getInventory());
        if (bestSlot < 0)
            return false;
        if (bestSlot == FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())
            return false;
        int newSlot = FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot() - 36;
        FishingBot.getInstance().getCurrentBot().getPlayer().swapToHotBar(bestSlot, newSlot);
        return true;
    }

    public void finishedLooking() {
        if (isPaused()) return;
        if (trackingNextBobberId) return;
        if (currentBobber != null) return;
        new Thread(() -> {
            setTrackingNextBobberId(true);
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
        }).start();
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        if (isJoined())
            return;
        this.joined = true;
        new Thread(() -> {
            if (isPaused())
                return;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) {
            }
            setTrackingNextBobberId(true);
            if (!ItemUtils.isFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem()))
                noRod();
            else {
                FishingBot.getI18n().info("module-fishing-start-fishing");
                if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking())
                    return;
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
            }
        }).start();
    }

    @EventHandler
    public void onEntityVelocity(EntityVelocityEvent event) {
        getPossibleCaughtItems().updateCaught(event.getEid(), null, null, null, event.getX(), event.getY(), event.getZ());
        if (getCurrentBobber() == null)
            return;
        if (getCurrentBobber().getEntityId() != event.getEid())
            return;
        // Prevent Velocity grabbed from flying hook
        if (!getCurrentBobber().existsForAtLeast(2500))
            return;

        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();
        if (protocolId <= ProtocolConstants.MINECRAFT_1_10) {
            FishingBot.getInstance().getCurrentBot().getFishingModule().fish();
        } else {
            if (Math.abs(event.getY()) > 350) {
                FishingBot.getInstance().getCurrentBot().getFishingModule().fish();
            } else if (lastY == 0 && event.getY() == 0) {    //Sometimes Minecraft does not push the bobber down, but this workaround works good
                FishingBot.getInstance().getCurrentBot().getFishingModule().fish();
            }
        }

        lastY = event.getY();
    }

    @EventHandler
    public void onBobberMove(EntityMoveEvent event) {
        if (getCurrentBobber() == null) return;
        if (getCurrentBobber().getEntityId() != event.getEntityId()) return;

        getCurrentBobber().move(event.getDX(), event.getDY(), event.getDZ());
    }

    @EventHandler
    public void onBobberTeleport(EntityTeleportEvent event) {
        if (getCurrentBobber() == null) return;
        if (getCurrentBobber().getEntityId() != event.getEntityId()) return;

        getCurrentBobber().teleport(event.getX(), event.getY(), event.getZ());
    }

    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
        if (event.getWindowId() != 0)
            return;
        Slot slot = event.getSlot();

        updateInventory(slot, event.getSlotId());
    }

    @EventHandler
    public void onUpdateWindowItems(UpdateWindowItemsEvent event) {
        if (event.getWindowId() != 0)
            return;
        for (int i = 0; i < event.getSlots().size(); i++) {
            if (ItemUtils.isFishingRod(event.getSlots().get(i))) {
                updateInventory(event.getSlots().get(i), i);
                break;
            }
        }
    }

    private void updateInventory(Slot slot, int slotId) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            // check current fishing rod value and swap if a better one is in inventory
            if (ItemUtils.isFishingRod(slot) && !FishingBot.getInstance().getCurrentBot().getConfig().isDisableRodChecking()) {
                swapWithBestFishingRod();
            }

            if (isPaused())
                return;

            if (FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot() == slotId) {
                if (isNoRodAvailable() && ItemUtils.isFishingRod(slot)) {
                    if (FishingBot.getInstance().getCurrentBot().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(slot) >= 63)
                        return;
                    FishingBot.getI18n().info("module-fishing-new-rod-available");
                    setLastFish(System.currentTimeMillis());
                    setNoRodAvailable(false);
                    setCurrentBobber(null);
                    setTrackingNextEntityMeta(false);

                    if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking())
                        return;
                    setTrackingNextBobberId(true);
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
                } else if (!isNoRodAvailable() && !ItemUtils.isFishingRod(slot)) {
                    noRod();
                }
                this.currentFishingRodValue = ItemUtils.getFishingRodValue(slot);
            }
        }).start();
    }

    @EventHandler
    public void onSpawnBobber(SpawnEntityEvent event) {
        if (!isTrackingNextBobberId())
            return;

        if (FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID() != -1 && event.getObjectData() != FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID())
            return;

        if (event.getType() == BOBBER_ENTITY_TYPE) {
            reFish(event.getId(), event.getX(), event.getY(), event.getZ());
        }
    }

    @EventHandler
    public void onSpawnFish(SpawnEntityEvent event) {
        if (!isTrackingNextEntityMeta()) return;
        if (event.getType() != ITEM_ENTITY_TYPE) return;
        getPossibleCaughtItems().addCaught(new Item(event.getId(), null, null, null, event.getXVelocity(), event.getYVelocity(), event.getZVelocity(), event.getX(), event.getY(), event.getZ()));
    }

    @EventHandler
    public void onDestroy(DestroyEntitiesEvent event) {
        if (getCurrentBobber() != null && event.getEntityIds().contains(getCurrentBobber().getEntityId())) {
            setCurrentBobber(null);
            stuck();
        }
    }

    @EventHandler
    public void onHookEntity(EntityDataEvent event) {
        if (currentBobber == null) return;
        if (event.getEntityId() != currentBobber.getEntityId()) return;
        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();
        event.getData().stream().filter(entityDataValue -> entityDataValue.getElement().getInternalId().equals("varint")).forEach(entityDataValue -> {
            boolean hookedEntity = false;
            if (protocolId <= ProtocolConstants.MINECRAFT_1_9_4 && entityDataValue.getElementIndex() == 5)
                hookedEntity = true;
            else if (protocolId <= ProtocolConstants.MINECRAFT_1_13_2 && entityDataValue.getElementIndex() == 6)
                hookedEntity = true;
            else if (protocolId <= ProtocolConstants.MINECRAFT_1_16_4 && entityDataValue.getElementIndex() == 7)
                hookedEntity = true;
            else if (protocolId <= ProtocolConstants.MINECRAFT_1_20_5 && entityDataValue.getElementIndex() == 8)
                hookedEntity = true;
            if (hookedEntity && ((int)entityDataValue.getElement().getValue()) > 0) {
                FishingBot.getI18n().warning("module-fishing-entity-hooked");
                setPaused(true);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignore) {}
                setPaused(false);
            }
        });
    }

    // Stucking fix
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (isJoined() && System.currentTimeMillis() - getLastFish() > 60000) {
                setLastFish(System.currentTimeMillis());
                if (isNoRodAvailable())
                    continue;
                if (isPaused())
                    continue;
                Slot curr = FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem();
                if (ItemUtils.isFishingRod(curr) && ItemUtils.getDamage(curr) >= 63) {
                    noRod();
                    continue;
                }
                setCurrentBobber(null);
                setTrackingNextEntityMeta(false);
                FishingBot.getI18n().warning("module-fishing-bot-is-slow");

                if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking())
                    return;
                setTrackingNextBobberId(true);
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @EventHandler
    public void onEntityData(EntityDataEvent event) {
        event.getData().stream()
                .map(EntityDataValue::getElement)
                .filter(element -> element.getInternalId().equals("slot"))
                .filter(element -> element.getValue() instanceof Slot)
                .map(element -> (Slot) element.getValue())
                .forEach(slot -> {
                    List<Enchantment> enchantments = ItemUtils.getEnchantments(slot);
                    String name = ItemUtils.getItemName(slot);
                    FishingBot.getInstance().getCurrentBot().getFishingModule().getPossibleCaughtItems().updateCaught(event.getEntityId(), name, slot.getItemId(), enchantments, -1, -1, -1);
                });
    }
}
