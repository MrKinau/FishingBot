/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.fishing;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.Manager;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseItem;
import systems.kinau.fishingbot.network.utils.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class FishingManager extends Manager implements Runnable {

    private static final List<Integer> FISH_IDS_1_14 = Arrays.asList(625, 626, 627, 628);
    private static final List<Integer> FISH_IDS_1_8 = Collections.singletonList(349);

    public FishingManager() {
        new Thread(this).start();
    }

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
        getNetworkHandler().sendPacket(new PacketOutUseItem(getNetworkHandler()));
        new Thread(() -> {
            try {
                Thread.sleep(200);
                setTrackingNextEntityMeta(false);
                getCaughtItem();
                Thread.sleep(200);
                setTrackingNextFishingId(true);
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
                getNetworkHandler().sendPacket(new PacketOutUseItem(getNetworkHandler()));
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
                MineBot.getConfig().getAnnounceTypeConsole(),
                MineBot.getLog()::info,
                MineBot.getLog()::info);

        //Print in mc chat (based on announcetype)
        logItem(currentMax,
                MineBot.getConfig().getAnnounceTypeChat(),
                (String str) -> getNetworkHandler().sendPacket(new PacketOutChat(MineBot.PREFIX + str)),
                (String str) -> {
                    // Delay the enchant messages to arrive after the item announcement
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getNetworkHandler().sendPacket(new PacketOutChat(str));
                });
    }


    private String stringify(Item item) {
        return "Caught \"" + item.getName() + "\"";
    }

    private void logItem(Item item, AnnounceType noisiness, Consumer<String> announce, Consumer<String> announceEnchants) {
        if (noisiness == AnnounceType.NONE)
            return;
        else if (noisiness == AnnounceType.ALL)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_14.contains(item.getItemId()) && !FISH_IDS_1_8.contains(item.getItemId()))
            announce.accept(stringify(item));

        if (item.getEnchantments().isEmpty())
            return;

        if (noisiness == AnnounceType.ONLY_ENCHANTED)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ONLY_BOOKS && item.getItemId() == 779)
            announce.accept(stringify(item));
        if (noisiness == AnnounceType.ONLY_BOOKS && item.getItemId() != 779)
            return;

        if (!item.getEnchantments().isEmpty()) {
            for (Map<String, Short> enchantment : item.getEnchantments()) {
                enchantment.keySet().forEach(s -> {
                    String asText = "-> "
                            + s.replace("minecraft:", "").toUpperCase()
                            + " "
                            + getRomanLevel(enchantment.get(s));
                    announceEnchants.accept(asText);
                });
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
                getNetworkHandler().sendPacket(new PacketOutUseItem(getNetworkHandler()));
                MineBot.getLog().warning("Bot is slow (Maybe stuck). Trying to restart!");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected() {
        setTrackingNextFishingId(true);
        synchronized (MineBot.getLog()) {
            getNetworkHandler().sendPacket(new PacketOutUseItem(getNetworkHandler()));
            MineBot.getLog().info("Starting fishing!");
            if(MineBot.getServerProtocol() == ProtocolConstants.MINECRAFT_1_8)
                startPositionUpdate(getNetworkHandler());
        }
    }
}
