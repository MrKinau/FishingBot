/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.event.play.DifficultySetEvent;
import systems.kinau.fishingbot.event.play.SpawnPlayerEvent;
import systems.kinau.fishingbot.network.protocol.Replayer;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutPosition;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseEntity;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseItem;

public class CosmicSkyModule extends Module implements Listener {

    @Getter private boolean joinedIslands = false;
    @Getter private boolean joinedStiansIsland = false;
    @Getter private boolean arrivedAtPortal = false;
    @Getter private Thread currentThread;
    @Getter private int fishermanEid;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() { }

    private void movingToPortal() {
        currentThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (FishingBot.getInstance().getPlayer().getZ() < -6406.2) {
                    Player player = FishingBot.getInstance().getPlayer();
                    player.setZ(player.getZ() + 0.15);
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutPosition(player.getX(), player.getY(), player.getZ(), true));
                } else if (FishingBot.getInstance().getPlayer().getX() > -494735.5){
                    Player player = FishingBot.getInstance().getPlayer();
                    player.setX(player.getX() - 0.15);
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutPosition(player.getX(), player.getY(), player.getZ(), true));
                } else {
                    FishingBot.getLog().info("Arrived at portal!");
                    arrivedAtPortal = true;
                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        currentThread.start();
    }

    private void movingToFisherMan() {
        currentThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(FishingBot.getInstance().getPlayer().getX() + "/" + FishingBot.getInstance().getPlayer().getY() + "/" + FishingBot.getInstance().getPlayer().getZ());
                if (FishingBot.getInstance().getPlayer().getY() > 228.0625) {
                    Player player = FishingBot.getInstance().getPlayer();
                    player.setY(player.getY() - 0.1);
                    player.setX(player.getX() + 0.15);
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutPosition(player.getX(), player.getY(), player.getZ(), false));
                } else {
                    new Replayer("walkToFisherman.packets").replay();
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutUseEntity(fishermanEid, 0, 0, 0, 0, 0));
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutUseEntity(fishermanEid, 2, -0.4F, 0.79F, -0.14F, 0));
                    new Replayer("walkToLake.packets").replay();
                    FishingBot.getInstance().setFishingModule(new FishingModule());
                    FishingBot.getInstance().getFishingModule().enable();
                    FishingBot.getInstance().getFishingModule().setTrackingNextFishingId(true);
                    FishingBot.getInstance().getNet().sendPacket(new PacketOutUseItem(FishingBot.getInstance().getNet()));
                    FishingBot.getLog().info("Starting fishing!");
                    break;
                }
            }
        });
        currentThread.start();
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getText().equalsIgnoreCase("(!) You are already on this island!")) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                movingToPortal();
            }).start();
        } else if (event.getText().equalsIgnoreCase("(!) Traveling to realm...")) {
            new Thread(() -> {
                getCurrentThread().interrupt();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getCurrentThread().interrupt();
                movingToFisherMan();
            }).start();
        } else if(event.getText().contains("Daily Realm Playtimes will reset in")) {
            FishingBot.getLog().info("Fishing realm playtime is over!");
            FishingBot.getInstance().setRunning(false);
        }
    }

    @EventHandler
    public void onDifficultySet(DifficultySetEvent event) {
        if(arrivedAtPortal)
            return;
        if (isJoinedStiansIsland()) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                movingToPortal();
            }).start();
            return;
        }
        if (isJoinedIslands()) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FishingBot.getLog().info("WARP to island");
                FishingBot.getInstance().getNet().sendPacket(new PacketOutChat("/is warp stian2004"));
                joinedStiansIsland = true;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isArrivedAtPortal()) {
                    FishingBot.getLog().severe("Error to arrive at portal");
                    FishingBot.getInstance().setRunning(false);
                }
            }).start();
            return;
        }
        FishingBot.getLog().info("JOIN the island lobby");
        FishingBot.getInstance().getNet().sendPacket(new PacketOutChat("/join"));
        joinedIslands = true;
    }

    @EventHandler
    public void onSpawnPlayer(SpawnPlayerEvent event) {
        if(!isArrivedAtPortal())
            return;
        if(event.getX() == 37.5 && event.getY() == 230 && event.getZ() == 22.5)
            fishermanEid = event.getEID();
    }
}
