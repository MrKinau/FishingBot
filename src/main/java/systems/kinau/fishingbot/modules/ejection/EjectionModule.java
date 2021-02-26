package systems.kinau.fishingbot.modules.ejection;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Inventory;
import systems.kinau.fishingbot.bot.Player;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.modules.Module;
import systems.kinau.fishingbot.utils.ItemUtils;
import systems.kinau.fishingbot.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class EjectionModule extends Module {

    private List<LookEjectFunction> lookEjectFunctions = new ArrayList<>();
    private List<ChestEjectFunction> chestEjectFunctions = new ArrayList<>();

    private Thread chestFillingThread;

    @Override
    public void onEnable() {
        this.lookEjectFunctions = new ArrayList<>();
        this.chestEjectFunctions = new ArrayList<>();
    }

    @Override
    public void onDisable() {
        this.lookEjectFunctions = null;
        this.chestEjectFunctions = null;
        if (this.chestFillingThread != null)
            this.chestFillingThread.interrupt();
        this.chestFillingThread = null;
    }

    public void executeEjectionRules(List<EjectionRule> ejectionRules, Slot updatedItem, short slotId) {
        if (!updatedItem.isPresent())
            return;
        Player player = FishingBot.getInstance().getCurrentBot().getPlayer();
        String itemName = ItemUtils.getItemName(updatedItem);
        for (EjectionRule ejectionRule : ejectionRules) {
            if (ejectionRule.getAllowList().contains(itemName)) {
                switch (ejectionRule.getEjectionType()) {
                    case FILL_CHEST: {
                        for (ChestEjectFunction chestEjectFunction : chestEjectFunctions) {
                            if (chestEjectFunction.getSlot() == slotId)
                                return;
                        }

                        ChestEjectFunction chestEjectFunction = new ChestEjectFunction(ejectionRule.getDirection(), slotId);
                        chestEjectFunctions.add(chestEjectFunction);
                        fillAdjacentChest(chestEjectFunction);
                        return;
                    }
                    case DROP:
                    default: {
                        for (LookEjectFunction lookEjectFunction : lookEjectFunctions) {
                            if (lookEjectFunction.getSlot() == slotId)
                                return;
                        }
                        LocationUtils.Direction direction = ejectionRule.getDirection();
                        float yaw = direction.getYaw() == Float.MIN_VALUE ? player.getYaw() : direction.getYaw();
                        float pitch = direction.getPitch() == Float.MIN_VALUE ? player.getPitch() : direction.getPitch();
                        LookEjectFunction lookEjectFunction = new LookEjectFunction(yaw, pitch, FishingBot.getInstance().getCurrentBot().getConfig().getLookSpeed(), slotId);
                        lookEjectFunctions.add(lookEjectFunction);
                        lookAndDrop(lookEjectFunction);
                        return;
                    }
                }
            }
        }
    }

    public List<LookEjectFunction> getLookEjectFunctions(float yaw, float pitch) {
        if (lookEjectFunctions == null)
            return Collections.emptyList();
        return lookEjectFunctions.stream()
                .filter(lookEjectFunction -> lookEjectFunction.getYaw() == yaw)
                .filter(lookEjectFunction -> lookEjectFunction.getPitch() == pitch)
                .collect(Collectors.toList());
    }

    public List<ChestEjectFunction> getChestEjectFunctions(LocationUtils.Direction direction) {
        if (chestEjectFunctions == null)
            return Collections.emptyList();
        return chestEjectFunctions.stream()
                .filter(chestEjectFunction -> chestEjectFunction.getDirection() == direction)
                .collect(Collectors.toList());
    }

    private void lookAndDrop(LookEjectFunction lookEjectFunction) {
        Player player = FishingBot.getInstance().getCurrentBot().getPlayer();
        player.look(lookEjectFunction.getYaw(), lookEjectFunction.getPitch(), lookEjectFunction.getSpeed(), finished -> {
            List<LookEjectFunction> fittingFunctions = getLookEjectFunctions(lookEjectFunction.getYaw(), lookEjectFunction.getPitch());
            fittingFunctions.forEach(fittingFunction -> {
                player.dropStack(fittingFunction.getSlot(), (short) (fittingFunction.getSlot() - 8));
            });

            player.look(player.getOriginYaw(), player.getOriginPitch(), lookEjectFunction.getSpeed(), finished2 -> {
                FishingBot.getInstance().getCurrentBot().getFishingModule().finishedLooking();
                lookEjectFunctions.removeAll(fittingFunctions);
                if (!lookEjectFunctions.isEmpty())
                    lookAndDrop(lookEjectFunctions.get(0));
            });

        });
    }

    private void fillAdjacentChest(ChestEjectFunction chestEjectFunction) {
        if (chestFillingThread != null && chestFillingThread.isAlive()) {
            return;
        }

        this.chestFillingThread = new Thread(() -> {
            internalFillAdjacentChest(chestEjectFunction);
        });
        chestFillingThread.start();
    }

    private void internalFillAdjacentChest(ChestEjectFunction chestEjectFunction) {
        Player player = FishingBot.getInstance().getCurrentBot().getPlayer();

        player.openAdjacentChest(chestEjectFunction.getDirection());
        try {
            Thread.sleep(50 + player.getLastPing());
        } catch (InterruptedException ignore) { }
        List<ChestEjectFunction> fittingFunctions = getChestEjectFunctions(chestEjectFunction.getDirection());
        fittingFunctions.forEach(fittingFunction -> {
            for (Inventory inventory : player.getOpenedInventories().values()) {
                player.shiftToInventory(fittingFunction.getSlot(), inventory);
                inventory.setActionCounter((short) (inventory.getActionCounter() + 1));
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) { }
        });
        for (Integer window : new HashSet<>(player.getOpenedInventories().keySet())) {
            player.closeInventory(window);
        }

        chestEjectFunctions.removeAll(fittingFunctions);
        if (!chestEjectFunctions.isEmpty())
            internalFillAdjacentChest(chestEjectFunctions.get(0));
    }
}
