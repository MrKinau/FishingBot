/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseItem;
import systems.kinau.fishingbot.network.utils.Enchantment;
import systems.kinau.fishingbot.network.utils.Item;
import systems.kinau.fishingbot.network.utils.ItemUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

// TODO: Change fishing ids based on current version
public class FishingModule extends Module implements Runnable, Listener {

    private static final List<Integer> FISH_IDS_1_16 = Arrays.asList(687, 688, 689, 690);
    private static final List<Integer> FISH_IDS_1_14 = Arrays.asList(625, 626, 627, 628);
    private static final List<Integer> FISH_IDS_1_8 = Collections.singletonList(349);
    private static final List<Integer> ENCHANTED_BOOKS_IDS = Arrays.asList(779, 780, 847, 848);

    @Getter private List<Item> possibleCaughtItems = new CopyOnWriteArrayList<>();

    @Getter @Setter private int currentBobber = -1;
    @Getter @Setter private short lastY = -1;
    @Getter @Setter private boolean trackingNextBobberId = false;
    @Getter @Setter private boolean noRodAvailable = false;
    @Getter private boolean trackingNextEntityMeta = false;
    @Getter @Setter long lastFish = System.currentTimeMillis();

    @Getter @Setter private int currentFishingRodValue;

    @Getter private Thread stuckingFix;
    @Getter private boolean joined;

    public void setTrackingNextEntityMeta(boolean trackingNextEntityMeta) {
        this.trackingNextEntityMeta = trackingNextEntityMeta;
    }

    @Override
    public void onEnable() {
        FishingBot.getInstance().getEventManager().registerListener(this);
        if (FishingBot.getInstance().getConfig().isStuckingFixEnabled()) {
            stuckingFix = new Thread(this);
            stuckingFix.start();
        }
    }

    @Override
    public void onDisable() {
        if (stuckingFix != null)
            stuckingFix.interrupt();
        FishingBot.getInstance().getEventManager().unregisterListener(this);
    }

    public void stuck() {
        if (isNoRodAvailable())
            return;
        setLastFish(System.currentTimeMillis());
        setTrackingNextBobberId(true);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
    }

    public void fish() {
        setLastFish(System.currentTimeMillis());
        setCurrentBobber(-1);
        setTrackingNextEntityMeta(true);
        FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
        new Thread(() -> {
            try {
                int timeToWait = FishingBot.getInstance().getPlayer().getLastPing() + 200;
                Thread.sleep(timeToWait);
                setTrackingNextEntityMeta(false);
                getCaughtItem();
                Thread.sleep(200);
                setTrackingNextBobberId(true);
                Thread.sleep(200);
                if (FishingBot.getInstance().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(FishingBot.getInstance().getPlayer().getHeldItem()) >= 63) {
                    noRod();
                    return;
                }
                FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean containsPossibleItem(int eid) {
        return getPossibleCaughtItems().stream().anyMatch(item -> item.getEid() == eid);
    }

    public void addPossibleMotion(int eid, int motX, int motY, int motZ) {
        getPossibleCaughtItems().forEach(item -> {
            if(item.getEid() == eid) {
                item.setMotX(motX);
                item.setMotY(motY);
                item.setMotZ(motZ);
            }
        });
    }

    private void getCaughtItem() {
        if(getPossibleCaughtItems().size() < 1)
            return;
        Item currentMax = getPossibleCaughtItems().get(0);
        int currentMaxMot = getMaxMot(currentMax);
        for (Item possibleCaughtItem : getPossibleCaughtItems()) {
            int mot = getMaxMot(possibleCaughtItem);
            if(mot > currentMaxMot) {
                currentMax = possibleCaughtItem;
                currentMaxMot = mot;
            }
        }

        //Clear mem
        getPossibleCaughtItems().clear();

        //Print to console (based on announcetype)
        logItem(currentMax,
                FishingBot.getInstance().getConfig().getAnnounceTypeConsole(),
                FishingBot.getLog()::info,
                FishingBot.getLog()::info);

        //Print in mc chat (based on announcetype)
        logItem(currentMax,
                FishingBot.getInstance().getConfig().getAnnounceTypeChat(),
                (String str) -> FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(FishingBot.PREFIX + str)),
                (String str) -> {
                    // Delay the enchant messages to arrive after the item announcement
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(str));
                });

        FishingBot.getInstance().getEventManager().callEvent(new FishCaughtEvent(currentMax));
    }


    private String stringify(Item item) {
        return FishingBot.getI18n().t("module-fishing-caught-item", item.getName());
    }

    public void logItem(Item item, AnnounceType noisiness, Consumer<String> announce, Consumer<String> announceEnchants) {
        if (noisiness == AnnounceType.NONE)
            return;
        else if (noisiness == AnnounceType.ALL)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_14.contains(item.getItemId()) && !FISH_IDS_1_8.contains(item.getItemId())&& !FISH_IDS_1_16.contains(item.getItemId()))
            announce.accept(stringify(item));

        if (item.getEnchantments().isEmpty())
            return;

        if (noisiness == AnnounceType.ONLY_ENCHANTED)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ONLY_BOOKS && ENCHANTED_BOOKS_IDS.contains(item.getItemId()))
            announce.accept(stringify(item));
        if (noisiness == AnnounceType.ONLY_BOOKS && !ENCHANTED_BOOKS_IDS.contains(item.getItemId()))
            return;

        if (!item.getEnchantments().isEmpty()) {
            for (Enchantment enchantment : item.getEnchantments()) {
                String asText = "-> "
                        + enchantment.getEnchantmentType().getName().toUpperCase()
                        + " "
                        + getRomanLevel(enchantment.getLevel());
                announceEnchants.accept(asText);
            }
        }
    }

    private String getRomanLevel(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "" + number;
        }
    }

    private int getMaxMot(Item item) {
        return Math.abs(item.getMotX()) + Math.abs(item.getMotY()) + Math.abs(item.getMotZ());
    }

    private void noRod() {
        boolean swapped = swapWithBestFishingRod();
        if (!swapped) {
            this.noRodAvailable = true;

            FishingBot.getI18n().warning("module-fishing-no-rod-available");
        }
    }

    private void reFish(int id) {
        setTrackingNextBobberId(false);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException ignored) { }     //Prevent Velocity grabbed from flying hook
            setCurrentBobber(id);
        }).start();
    }

    private boolean swapWithBestFishingRod() {
        int bestSlot = ItemUtils.getBestFishingRod(FishingBot.getInstance().getPlayer().getInventory());
        if (bestSlot < 0)
            return false;
        if (bestSlot == FishingBot.getInstance().getPlayer().getHeldSlot())
            return false;
        int newSlot = FishingBot.getInstance().getPlayer().getHeldSlot() - 36;
        FishingBot.getInstance().getPlayer().swapToHotBar(bestSlot, newSlot);
        return true;
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        if (isJoined())
            return;
        this.joined = true;
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setTrackingNextBobberId(true);
            if (!ItemUtils.isFishingRod(FishingBot.getInstance().getPlayer().getHeldItem()))
                noRod();
            else {
                FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
                FishingBot.getI18n().info("module-fishing-start-fishing");
            }
        }).start();
    }

    //TODO: Caught detection may be much easier with the Is Caught field in EntityMetadataPacket (since MC-1.16)
    @EventHandler
    public void onEntityVelocity(EntityVelocityEvent event) {
        addPossibleMotion(event.getEid(), event.getX(), event.getY(), event.getZ());
        if (getCurrentBobber() != event.getEid())
            return;

        switch (FishingBot.getInstance().getServerProtocol()) {
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_8: {
                FishingBot.getInstance().getFishingModule().fish();
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                if (Math.abs(event.getY()) > 350) {
                    FishingBot.getInstance().getFishingModule().fish();
                } else if (lastY == 0 && event.getY() == 0) {    //Sometimes Minecraft does not push the bobber down, but this workaround works good
                    FishingBot.getInstance().getFishingModule().fish();
                }
                break;
            }
        }

        lastY = event.getY();
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
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            // check current fishing rod value and swap if a better one is in inventory
            if (ItemUtils.isFishingRod(slot))
                swapWithBestFishingRod();

            if (FishingBot.getInstance().getPlayer().getHeldSlot() == slotId) {
                if (isNoRodAvailable() && ItemUtils.isFishingRod(slot)) {
                    if (FishingBot.getInstance().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(slot) >= 63)
                        return;
                    setLastFish(System.currentTimeMillis());
                    setNoRodAvailable(false);
                    setCurrentBobber(-1);
                    setTrackingNextEntityMeta(false);
                    setTrackingNextBobberId(true);
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
                    FishingBot.getI18n().info("module-fishing-new-rod-available");
                } else if (!isNoRodAvailable() && !ItemUtils.isFishingRod(slot)) {
                    noRod();
                }
                this.currentFishingRodValue = ItemUtils.getFishingRodValue(slot);
            }
        }).start();
    }

    @EventHandler
    public void onSpawnObject(SpawnObjectEvent event) {
        if(!FishingBot.getInstance().getFishingModule().isTrackingNextBobberId())
            return;

        //TODO: Refactor just make the objecttype with if-constructs
        switch (FishingBot.getInstance().getServerProtocol()) {
            case ProtocolConstants.MINECRAFT_1_8:
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9: {
                if(event.getType() == 90) {   //90 = bobber
                    if(FishingBot.getInstance().getPlayer().getEntityID() == -1 || event.getObjectData() == FishingBot.getInstance().getPlayer().getEntityID())
                        reFish(event.getId());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4: {
                if(event.getType() == 101) {   //101 = bobber
                    if(FishingBot.getInstance().getPlayer().getEntityID() == -1 || event.getObjectData() == FishingBot.getInstance().getPlayer().getEntityID())
                        reFish(event.getId());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_15:
            case ProtocolConstants.MINECRAFT_1_15_1:
            case ProtocolConstants.MINECRAFT_1_15_2: {
                if(event.getType() == 102) {   //102 = bobber
                    if(FishingBot.getInstance().getPlayer().getEntityID() == -1 || event.getObjectData() == FishingBot.getInstance().getPlayer().getEntityID())
                        reFish(event.getId());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_16:
            case ProtocolConstants.MINECRAFT_1_16_1: {
                if(event.getType() == 106) {   //106 = bobber
                    if(FishingBot.getInstance().getPlayer().getEntityID() == -1 || event.getObjectData() == FishingBot.getInstance().getPlayer().getEntityID())
                        reFish(event.getId());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_16_2:
            case ProtocolConstants.MINECRAFT_1_16_3:
            case ProtocolConstants.MINECRAFT_1_16_4:
            default: {
                if(event.getType() == 107) {   //107 = bobber
                    if(FishingBot.getInstance().getPlayer().getEntityID() == -1 || event.getObjectData() == FishingBot.getInstance().getPlayer().getEntityID())
                        reFish(event.getId());
                }
                break;
            }
        }
    }

    @EventHandler
    public void onDestroy(DestroyEntitiesEvent event) {
        if (getCurrentBobber() != -1 && event.getEntityIds().contains(getCurrentBobber()))
            stuck();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (System.currentTimeMillis() - getLastFish() > 60000) {
                setLastFish(System.currentTimeMillis());
                if (noRodAvailable)
                    continue;
                Slot curr = FishingBot.getInstance().getPlayer().getHeldItem();
                if (ItemUtils.isFishingRod(curr) && ItemUtils.getDamage(curr) >= 63) {
                    noRod();
                    continue;
                }
                setCurrentBobber(-1);
                setTrackingNextEntityMeta(false);
                setTrackingNextBobberId(true);
                FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));

                FishingBot.getI18n().warning("module-fishing-bot-is-slow");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
