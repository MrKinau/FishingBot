package systems.kinau.fishingbot.network.entity;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.bot.registry.legacy.LegacyMaterial;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.ItemUtils;

import java.util.List;

/**
 * This is only used for 1.8
 */
public class LegacyDataParser {

    public void readEntityData(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int entityId, int protocolId) {
        while (true) {
            if (in.getAvailable() == 0)
                break;
            byte var2 = in.readByte();
            if (var2 == 0x7F)
                break;

            int i = (var2 & 224) >> 5;

            try {
                switch (i) {
                    case 0: {
                        in.readByte();
                        break;
                    }
                    case 1: {
                        in.readShort();
                        break;
                    }
                    case 2: {
                        in.readInt();
                        break;
                    }
                    case 3: {
                        float health = in.readFloat();
                        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(entityId, health, -1, -1));
                        return;
                    }
                    case 4: {
                        Packet.readString(in);
                        break;
                    }

                    case 5: {
                        Slot slot = Packet.readSlot(in, protocolId, networkHandler.getDataComponentRegistry());
                        String name = LegacyMaterial.getMaterialName(slot.getItemId(), Integer.valueOf(slot.getItemDamage()).shortValue());
                        List<Enchantment> enchantments = ItemUtils.getEnchantments(slot);
                        FishingBot.getInstance().getCurrentBot().getFishingModule().getPossibleCaughtItems().updateCaught(entityId, name, slot.getItemId(), enchantments, -1, -1, -1);

                        return;
                    }
                    case 6: {
                        in.readInt();
                        in.readInt();
                        in.readInt();
                        break;
                    }
                    case 7: {
                        in.readFloat();
                        in.readFloat();
                        in.readFloat();
                        break;
                    }
                }
            } catch (Exception ex) {
                break;
            }
        }
    }
}
