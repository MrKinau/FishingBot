/*
 * Created by David Luedtke (MrKinau)
 * 2019/11/2
 */

package systems.kinau.fishingbot.modules;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.JoinGameEvent;
import systems.kinau.fishingbot.event.play.UpdateSlotEvent;
import systems.kinau.fishingbot.mining.StripMining;
import systems.kinau.fishingbot.mining.World;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutDig;
import systems.kinau.fishingbot.network.protocol.play.PacketOutHeldItemChange;
import systems.kinau.fishingbot.network.utils.MaterialMc18;

public class MiningModule extends Module implements Listener {

    @Getter @Setter private boolean hasPickaxe = false;
    @Getter @Setter private boolean hasCobble = false;
    @Getter @Setter private boolean running = false;

    @Override
    public void onEnable() {
        MineBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        MineBot.getInstance().setWorld(new World(event.getDimension(), event.getLevelType()));
        start();
        new Thread(() -> {
            while (!Thread.interrupted()) {
                MineBot.getInstance().getPlayer().tick();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @EventHandler
    public void onSetSlot(UpdateSlotEvent event) {
        if (event.getWindowId() != 0)
            return;
        if (isHasPickaxe() && isHasCobble())
            return;
        if (!isRunning())
            return;
        if (event.getSlotId() < 36 || event.getSlotId() > 44)
            return;
        if (event.getItemStack() == null)
            return;

        if(event.getItemStack().getMaterial() == null) {
            dropItem(event.getSlotId());
            return;
        }

        if (!isHasPickaxe() && !event.getItemStack().getMaterial().isPickaxe()) {
            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("I don't like this shitty " + event.getItemStack().getMaterial().name() + ". All I want is a pickaxe!"));
            dropItem(event.getSlotId());
            return;
        }
        if (!isHasPickaxe() && event.getItemStack().getMaterial().isPickaxe()) {
            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("Thanks for this " + event.getItemStack().getMaterial().name()));
            setHasPickaxe(true);
            return;
        }

        if (isHasPickaxe() && !isHasCobble() && (event.getItemStack().getMaterial() != MaterialMc18.COBBLESTONE || event.getItemStack().getCount() < 64)) {
            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("I don't like this shitty " + event.getItemStack().getMaterial().name() + ". All I want is 64 cobblestone!"));
            dropItem(event.getSlotId());
            return;
        }
        if (isHasPickaxe() && !isHasCobble() && event.getItemStack().getMaterial() == MaterialMc18.COBBLESTONE && event.getItemStack().getCount() == 64) {
            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("Thanks for this " + event.getItemStack().getMaterial().name()));
            setHasCobble(true);
            return;
        }
    }

    public void start() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 36; i <= 44; i++) {
                switch (i) {
                    case 36: {
                        if (MineBot.getInstance().getPlayer().getInventory().getSlot(i) == null) {
                            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("Please equip my first slot with a pickaxe!"));
                            break;
                        } else if (MineBot.getInstance().getPlayer().getInventory().getSlot(i).getMaterial() == null || !MineBot.getInstance().getPlayer().getInventory().getSlot(i).getMaterial().isPickaxe()) {
                            dropItem(i);
                            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("Please equip my first slot with a pickaxe!"));
                            break;
                        }
                        setHasPickaxe(true);
                        break;
                    }
                    case 37: {
                        if (MineBot.getInstance().getPlayer().getInventory().getSlot(i) == null) {
                            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("Please equip my second slot with 64 cobblestone!"));
                            break;
                        } else if (MineBot.getInstance().getPlayer().getInventory().getSlot(i).getMaterial() != MaterialMc18.COBBLESTONE
                                || MineBot.getInstance().getPlayer().getInventory().getSlot(i).getCount() != 64) {
                            dropItem(i);
                            MineBot.getInstance().getNet().sendPacket(new PacketOutChat("Please equip my second slot with 64 cobblestone!"));
                            break;
                        }
                        setHasCobble(true);
                        break;
                    }
                    default: {
                        if (MineBot.getInstance().getPlayer().getInventory().getSlot(i) != null
                                && !MineBot.getInstance().getPlayer().getInventory().getSlot(i).getMaterial().isPickaxe()) {
                            dropItem(i);
                        }
                    }
                }
            }

            setRunning(true);

            while (!isHasPickaxe() || !isHasCobble()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            StripMining stripMining = new StripMining();

        }).start();
    }

    private void dropItem(int slot) {
        MineBot.getInstance().getNet().sendPacket(new PacketOutHeldItemChange((short)(slot - 36)));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MineBot.getInstance().getNet().sendPacket(new PacketOutDig((byte)3, 0, 0, 0, (byte)0));
    }
}
