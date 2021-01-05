/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.enums.MaterialMc18;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.utils.ItemUtils;

import java.util.List;

//TODO: Add as event, yes this code is ugly...
@NoArgsConstructor
public class PacketInEntityMetadata extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (FishingBot.getInstance().getCurrentBot().getFishingModule() == null)
            return;
        try {
            int eid = readVarInt(in);
            if (!FishingBot.getInstance().getCurrentBot().getFishingModule().isTrackingNextEntityMeta() && FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID() != eid)
                return;
            if (FishingBot.getInstance().getCurrentBot().getFishingModule().containsPossibleItem(eid) && FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID() != eid)
                return;
            if (protocolId == ProtocolConstants.MINECRAFT_1_8) {
                readWatchableObjects18(in, networkHandler, eid);
            } else {
                defaultLoop(protocolId, in, networkHandler, eid);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private void defaultLoop(int protocolID, ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int eid) {
        while (true) {
            if (in.getAvailable() == 0)
                break;
            int var2 = in.readByte();
            if (var2 == -1)
                break;
            if (var2 == 127 || in.getAvailable() <= 1) {
                break;
            }

            int type = in.readByte();

            switch (protocolID) {
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
                    readWatchableObjects19(in, networkHandler, eid, type);
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_13_1:
                case ProtocolConstants.MINECRAFT_1_13: {
                    readWatchableObjects113(in, networkHandler, eid, type);
                    break;
                }
                case ProtocolConstants.MINECRAFT_1_13_2:
                case ProtocolConstants.MINECRAFT_1_14:
                case ProtocolConstants.MINECRAFT_1_14_1:
                case ProtocolConstants.MINECRAFT_1_14_2:
                case ProtocolConstants.MINECRAFT_1_14_3:
                case ProtocolConstants.MINECRAFT_1_14_4:
                default: {
                    readWatchableObjects114(in, networkHandler, eid, type);
                    break;
                }
            }
        }
    }

    private void readWatchableObjects114(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int eid, int type) {
        switch (type) {
            case 0: {
                in.readByte();
                break;
            }
            case 1: {
                readVarInt(in);
                break;
            }
            case 2: {
                float health = in.readFloat();
                FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(eid, health, -1, -1));
                return;
            }
            case 3: {
                readString(in);
                break;
            }
            case 4: {
                readString(in);
                break;
            }
            case 5: {
                boolean present = in.readBoolean();
                if (present) {
                    readString(in);
                }
                break;
            }
            case 6: {
                Slot slot = readSlot(in);
                if (!slot.isPresent())
                    return;
                List<Enchantment> enchantments = ItemUtils.getEnchantments(slot);
                String name = ItemUtils.getItemName(slot);
                FishingBot.getInstance().getCurrentBot().getFishingModule().getPossibleCaughtItems().add(new Item(eid, slot.getItemId(), name, enchantments, -1, -1, -1));
                return;
            }
            case 7: {
                in.readBoolean();
                break;
            }
            case 8: {
                in.readFloat();
                in.readFloat();
                in.readFloat();
                break;
            }
            case 9: {
                in.readLong();
                break;
            }
            case 10: {
                boolean present = in.readBoolean();
                if (present) {
                    in.readLong();
                }
                break;
            }
            case 11: {
                readVarInt(in);
                break;
            }
            case 12: {
                boolean present = in.readBoolean();
                if (present) {
                    readUUID(in);
                }
                break;
            }
            case 13: {
                readVarInt(in);
                break;
            }
            case 14: {
                in.readFully(new byte[in.getAvailable()]);
                break;
            }
            case 15: {
                int id = readVarInt(in);
                switch (id) {
                    case 20:
                    case 3: {
                        readVarInt(in);
                        break;
                    }
                    case 11: {
                        in.readFloat();
                        in.readFloat();
                        in.readFloat();
                        in.readFloat();
                        break;
                    }
                    case 27: {
                        boolean present = in.readBoolean();
                        if (!present)
                            break;
                        readVarInt(in);
                        in.readByte();
                        in.readFully(new byte[in.getAvailable()]);
                        break;
                    }
                }
                break;
            }
            case 16: {
                readVarInt(in);
                readVarInt(in);
                readVarInt(in);
                break;
            }
            case 17: {
                readVarInt(in);
                break;
            }
            case 18: {
                readVarInt(in);
                break;
            }
        }
    }

    private void readWatchableObjects113(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int eid, int type) {
        try {
            switch (type) {
                case 0: {
                    in.readByte();
                    break;
                }
                case 1: {
                    readVarInt(in);
                    break;
                }
                case 2: {
                    float health = in.readFloat();
                    FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(eid, health, -1, -1));
                    return;
                }
                case 3: {
                    readString(in);
                    break;
                }
                case 4: {
                    readString(in);
                    break;
                }
                case 5: {
                    in.readBoolean();
                    break;
                }
                case 6: {
                    Slot slot = readSlot(in);
                    if (!slot.isPresent())
                        return;

                    List<Enchantment> enchantments = ItemUtils.getEnchantments(slot);

                    String name = ItemUtils.getItemName(slot);

                    FishingBot.getInstance().getCurrentBot().getFishingModule().getPossibleCaughtItems().add(new Item(eid, slot.getItemId(), name, enchantments, -1, -1, -1));

                    return;
                }
                case 7: {
                    in.readBoolean();
                    break;
                }
                case 8: {
                    in.readFloat();
                    in.readFloat();
                    in.readFloat();
                    break;
                }
                case 9: {
                    in.readLong();
                    break;
                }
                case 10: {
                    boolean present = in.readBoolean();
                    if (present) {
                        in.readLong();
                    }
                    break;
                }
                case 11: {
                    readVarInt(in);
                    break;
                }
                case 12: {
                    boolean present = in.readBoolean();
                    if (present) {
                        readUUID(in);
                    }
                    break;
                }
                case 13: {
                    readVarInt(in);
                    break;
                }
                case 14: {
                    in.readFully(new byte[in.getAvailable()]);
                    break;
                }
                case 15: {
                    int id = readVarInt(in);
                    switch (id) {
                        case 20:
                        case 3: {
                            readVarInt(in);
                            break;
                        }
                        case 11: {
                            in.readFloat();
                            in.readFloat();
                            in.readFloat();
                            in.readFloat();
                            break;
                        }
                        case 27: {
                            boolean present = in.readBoolean();
                            if (!present)
                                break;
                            readVarInt(in);
                            in.readByte();
                            in.readFully(new byte[in.getAvailable()]);
                            break;
                        }
                    }
                    break;
                }
                case 16: {
                    readVarInt(in);
                    readVarInt(in);
                    readVarInt(in);
                    break;
                }
                case 17: {
                    readVarInt(in);
                    break;
                }
                case 18: {
                    readVarInt(in);
                    break;
                }
            }
        } catch (Exception ignored) { }
    }

    private void readWatchableObjects19(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int eid, int type) {
        try {
            switch (type) {
                case 0: {
                    in.readByte();
                    break;
                }
                case 1: {
                    readVarInt(in);
                    break;
                }
                case 2: {
                    float health = in.readFloat();
                    FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(eid, health, -1, -1));
                    return;
                }
                case 3: {
                    readString(in);
                    break;
                }
                case 4: {
                    readString(in);
                    break;
                }
                case 5: {
                    Slot slot = readSlot(in);
                    if (!slot.isPresent())
                        return;
                    String name = ItemUtils.getItemName(slot);
                    List<Enchantment> enchantments = ItemUtils.getEnchantments(slot);
                    FishingBot.getInstance().getCurrentBot().getFishingModule().getPossibleCaughtItems().add(new Item(eid, slot.getItemId(), name, enchantments, -1, -1, -1));
                    return;
                }
                case 6: {
                    in.readBoolean();
                    break;
                }
                case 7: {
                    in.readFloat();
                    in.readFloat();
                    in.readFloat();
                    break;
                }
                case 8: {
                    in.readLong();
                    break;
                }
                case 9: {
                    boolean present = in.readBoolean();
                    if (present) {
                        in.readLong();
                    }
                    break;
                }
                case 10: {
                    readVarInt(in);
                    break;
                }
                case 11: {
                    boolean present = in.readBoolean();
                    if (present) {
                        readUUID(in);
                    }
                    break;
                }
                case 12: {
                    readVarInt(in);
                    break;
                }
            }
        } catch (Exception ignored) { }
    }

    private void readWatchableObjects18(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int eid) {
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
                        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new UpdateHealthEvent(eid, health, -1, -1));
                        return;
                    }
                    case 4: {
                        readString(in);
                        break;
                    }

                    case 5: {
                        Slot slot = readSlot(in);
                        String name = MaterialMc18.getMaterialName(slot.getItemId(), slot.getItemDamage());
                        List<Enchantment> enchantments = ItemUtils.getEnchantments(slot);
                        FishingBot.getInstance().getCurrentBot().getFishingModule().getPossibleCaughtItems().add(new Item(eid, slot.getItemId(), name, enchantments, -1, -1, -1));

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
