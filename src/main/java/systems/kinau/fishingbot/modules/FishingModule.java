/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.DifficultySetEvent;
import systems.kinau.fishingbot.event.play.EntityVelocityEvent;
import systems.kinau.fishingbot.event.play.SpawnObjectEvent;
import systems.kinau.fishingbot.event.play.UpdateSlotEvent;
import systems.kinau.fishingbot.fishing.AnnounceType;
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

public class FishingModule extends Module implements Runnable, Listener {

    private static final List<Integer> FISH_IDS_1_14 = Arrays.asList(625, 626, 627, 628);
    private static final List<Integer> FISH_IDS_1_8 = Collections.singletonList(349);

    @Getter private List<Item> possibleCaughtItems = new CopyOnWriteArrayList<>();

    @Getter @Setter private int currentBobber = -1;
    @Getter @Setter private short lastY = -1;
    @Getter @Setter private boolean trackingNextFishingId = false;
    @Getter @Setter private boolean trackingNextEntityMeta = false;
    @Getter @Setter long lastFish = System.currentTimeMillis();

    @Getter @Setter private int heldSlot;

    @Getter private Thread stuckingFix;

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

    public void fish() {
        setLastFish(System.currentTimeMillis());
        setCurrentBobber(-1);
        setTrackingNextEntityMeta(true);
        FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
        new Thread(() -> {
            try {
                Thread.sleep(200);
                setTrackingNextEntityMeta(false);
                getCaughtItem();
                Thread.sleep(200);
                setTrackingNextFishingId(true);
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
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

    private void noFishingRod() {
        FishingBot.getLog().severe("No fishing rod equipped. Retrying later!");
    }

    private void reFish(int id) {
        setTrackingNextFishingId(false);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException e) { }     //Prevent Velocity grabbed from flying hook
            setCurrentBobber(id);
        }).start();
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setTrackingNextFishingId(true);
            FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
            FishingBot.getLog().info("Starting fishing!");
        }).start();
    }

    //TODO: Caught detection may be much easier with the Is Caught field in EntityMetadataPacket (since MC-1.16)
    @EventHandler
    public void onEntityVelocity(EntityVelocityEvent event) {
        addPossibleMotion(event.getEid(), event.getX(), event.getY(), event.getZ());
        if(getCurrentBobber() != event.getEid())
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
                if(Math.abs(event.getY()) > 350) {
                    FishingBot.getInstance().getFishingModule().fish();
                } else if(lastY == 0 && event.getY() == 0) {    //Sometimes Minecraft does not push the bobber down, but this workaround works good
                    FishingBot.getInstance().getFishingModule().fish();
                }
                break;
            }
        }

        lastY = event.getY();
    }

    //TODO: Rewrite or delete including packets
    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
//        if(event.getWindowId() != 0)
//            return;
//        if(event.getSlotId() != FishingBot.getInstance().getPlayer().getHeldSlot())
//            return;
//        ByteArrayDataInputWrapper testFishRod = new ByteArrayDataInputWrapper(event.getSlotData().toByteArray().clone());
//        int protocolId = FishingBot.getInstance().getServerProtocol();
//        if(protocolId < ProtocolConstants.MINECRAFT_1_13) {
//            short itemId = testFishRod.readShort();
//            if (itemId != 346)  //Normal ID
//                noFishingRod();
//        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13) {
//            short itemId = testFishRod.readShort();
//            if (itemId != 563)  //ID in 1.13.0
//                noFishingRod();
//        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13_1) {
//            short itemId = testFishRod.readShort();
//            if (itemId != 568)  //ID in 1.13.1
//                noFishingRod();
//        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13_2) {
//            boolean present = testFishRod.readBoolean();
//            if(!present)
//                noFishingRod();
//            int itemId = Packet.readVarInt(testFishRod);
//            if (itemId != 568) //ID in 1.13.2
//                noFishingRod();
//        } else {
//            boolean present = testFishRod.readBoolean();
//            if(!present)
//                noFishingRod();
//            int itemId = Packet.readVarInt(testFishRod);
//            if (itemId != 622) //ID in 1.14
//                noFishingRod();
//        }
    }

    @EventHandler
    public void onSpawnObject(SpawnObjectEvent event) {
        if(!FishingBot.getInstance().getFishingModule().isTrackingNextFishingId())
            return;

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
            case ProtocolConstants.MINECRAFT_1_16_PRE_2:
            case ProtocolConstants.MINECRAFT_1_16_PRE_5:
            default: {
                if(event.getType() == 106) {   //106 = bobber
                    if(FishingBot.getInstance().getPlayer().getEntityID() == -1 || event.getObjectData() == FishingBot.getInstance().getPlayer().getEntityID())
                        reFish(event.getId());
                }
                break;
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if(System.currentTimeMillis() - getLastFish() > 60000) {
                setLastFish(System.currentTimeMillis());
                setCurrentBobber(-1);
                setTrackingNextEntityMeta(false);
                setTrackingNextFishingId(true);
                FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
                FishingBot.getLog().warning("Bot is slow (maybe stuck). Trying to restart!");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
