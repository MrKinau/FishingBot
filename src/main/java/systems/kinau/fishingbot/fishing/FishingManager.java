/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.fishing;

import com.google.common.io.ByteArrayDataOutput;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseItem;
import systems.kinau.fishingbot.network.utils.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FishingManager implements Runnable {

    private static final List<Integer> FISH_IDS_1_14 = Arrays.asList(625, 626, 627, 628);
    private static final List<Integer> FISH_IDS_1_8 = Collections.singletonList(349);

    public FishingManager() {
        new Thread(this).start();
    }

    @Getter @Setter private NetworkHandler networkHandler;
    @Getter private List<Item> possibleCaughtItems = new CopyOnWriteArrayList<>();

    @Getter @Setter private int currentBobber = -1;
    @Getter @Setter private boolean trackingNextFishingId = false;
    @Getter @Setter private boolean trackingNextEntityMeta = false;
    @Getter @Setter long lastFish = System.currentTimeMillis();

    @Getter @Setter ByteArrayDataOutput slotData;

    public void fish() {
        setLastFish(System.currentTimeMillis());
        setCurrentBobber(-1);
        setTrackingNextEntityMeta(true);
        networkHandler.sendPacket(new PacketOutUseItem(networkHandler));
        new Thread(() -> {
            try {
                Thread.sleep(200);
                setTrackingNextEntityMeta(false);
                getCaughtItem();
                Thread.sleep(200);
                setTrackingNextFishingId(true);
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
                networkHandler.sendPacket(new PacketOutUseItem(networkHandler));
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

        //Print to console whats caugth
        FishingBot.getLog().info("Caught \"" + currentMax.getName() + "\"");
        if (!currentMax.getEnchantments().isEmpty()) {
            for (Pair<String, Short> enchantment : currentMax.getEnchantments()) {
                FishingBot.getLog().info("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue()));
            }
        }

        //Print in mc chat (based on announcetype)

        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.NONE)
            return;
        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL)
            networkHandler.sendPacket(new PacketOutChat(FishingBot.PREFIX + "Caught: \"" + currentMax.getName() + "\""));
        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_14.contains(currentMax.getItemId()) && !FISH_IDS_1_8.contains(currentMax.getItemId()))
            networkHandler.sendPacket(new PacketOutChat(FishingBot.PREFIX + "Caught: \"" + currentMax.getName() + "\""));

        if (currentMax.getEnchantments().isEmpty())
            return;

        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_ENCHANTED)
            networkHandler.sendPacket(new PacketOutChat(FishingBot.PREFIX + "Caught: \"" + currentMax.getName() + "\""));
        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && currentMax.getItemId() == 779)
            networkHandler.sendPacket(new PacketOutChat(FishingBot.PREFIX + "Caught: \"" + currentMax.getName() + "\""));

        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && currentMax.getItemId() != 779)
            return;

        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

        if (!currentMax.getEnchantments().isEmpty()) {
            for (Pair<String, Short> enchantment : currentMax.getEnchantments()) {
                networkHandler.sendPacket(new PacketOutChat("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue())));
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

    @Override
    public void run() {
        while (true) {
            if(System.currentTimeMillis() - getLastFish() > 60000) {
                setLastFish(System.currentTimeMillis());
                setCurrentBobber(-1);
                setTrackingNextEntityMeta(false);
                setTrackingNextFishingId(true);
                networkHandler.sendPacket(new PacketOutUseItem(networkHandler));
                FishingBot.getLog().warning("Bot is slow (Maybe stuck). Trying to restart!");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
