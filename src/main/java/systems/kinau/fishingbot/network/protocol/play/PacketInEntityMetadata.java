/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.fishing.ItemHandler;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.Enchantments_1_8;
import systems.kinau.fishingbot.network.utils.Item;
import systems.kinau.fishingbot.network.utils.MaterialMc18;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//TODO: Add as event, yes this code is ugly...
@NoArgsConstructor
public class PacketInEntityMetadata extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (FishingBot.getInstance().getFishingModule() == null)
            return;
        int eid = readVarInt(in);
        if (!FishingBot.getInstance().getFishingModule().isTrackingNextEntityMeta() && FishingBot.getInstance().getPlayer().getEntityID() != eid)
            return;
        if (FishingBot.getInstance().getFishingModule().containsPossibleItem(eid) && FishingBot.getInstance().getPlayer().getEntityID() != eid)
            return;
        if (protocolId == ProtocolConstants.MINECRAFT_1_8) {
            readWatchableObjects18(in, networkHandler, eid);
        } else {
            defaultLoop(protocolId, in, networkHandler, eid);
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
                    FishingBot.getInstance().getEventManager().callEvent(new UpdateHealthEvent(health, -1, -1));
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
                    boolean present = in.readBoolean();
                    if (!present)
                        break;
                    int itemID = readVarInt(in);
                    byte count = in.readByte();
                    List<Map<String, Short>> enchantments = readNBT(in);
                    String name = ItemHandler.getItemName(itemID, FishingBot.getInstance().getServerProtocol()).replace("minecraft:", "");
                    FishingBot.getInstance().getFishingModule().getPossibleCaughtItems().add(new Item(eid, itemID, name, enchantments, -1, -1, -1));
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
        } catch (Exception ex) {
            ex.printStackTrace();
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
                    FishingBot.getInstance().getEventManager().callEvent(new UpdateHealthEvent(health, -1, -1));
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
                    int itemID = in.readShort();
                    byte count = in.readByte();
                    List<Map<String, Short>> enchantments = readNBT(in);

                    String name = ItemHandler.getItemName(itemID, FishingBot.getInstance().getServerProtocol()).replace("minecraft:", "");

                    FishingBot.getInstance().getFishingModule().getPossibleCaughtItems().add(new Item(eid, itemID, name, enchantments, -1, -1, -1));

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
                    FishingBot.getInstance().getEventManager().callEvent(new UpdateHealthEvent(health, -1, -1));
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
                    int itemID = in.readShort();
                    byte count = in.readByte();
                    short damage = in.readShort();
                    String name = MaterialMc18.getMaterial(itemID).name();
                    List<Map<String, Short>> enchantments = readNBT_1_8(in);

                    FishingBot.getInstance().getFishingModule().getPossibleCaughtItems().add(new Item(eid, itemID, name, enchantments, -1, -1, -1));

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
                        FishingBot.getInstance().getEventManager().callEvent(new UpdateHealthEvent(health, -1, -1));
                        return;
                    }
                    case 4: {
                        readString(in);
                        break;
                    }

                    case 5: {
                        int itemID = in.readShort();
                        in.readByte(); //count
                        in.readShort(); //damage
                        String name = MaterialMc18.getMaterial(itemID).name();
                        List<Map<String, Short>> enchantments = readNBT_1_8(in);

                        FishingBot.getInstance().getFishingModule().getPossibleCaughtItems().add(new Item(eid, itemID, name, enchantments, -1, -1, -1));

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

    private List<Map<String, Short>> readNBT(ByteArrayDataInputWrapper in) {

        byte[] bytes = new byte[in.getAvailable()];
        in.readFully(bytes);

        List<Map<String, Short>> enchList = new ArrayList<>();

        try {
            NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(bytes), false);
            Tag tag = nbtInputStream.readTag();
            if (tag.getType() == TagType.TAG_COMPOUND) {
                if (tag.getValue() instanceof CompoundMap) {
                    CompoundMap root = (CompoundMap) tag.getValue();
                    if (root.containsKey("StoredEnchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("StoredEnchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(Collections.singletonMap((String) enchant.getValue().get("id").getValue(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    } else if (root.containsKey("Enchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("Enchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(Collections.singletonMap((String) enchant.getValue().get("id").getValue(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return enchList;
    }

    private List<Map<String, Short>> readNBT_1_8(ByteArrayDataInputWrapper in) {

        byte[] bytes = new byte[in.getAvailable()];
        in.readFully(bytes);

        List<Map<String, Short>> enchList = new ArrayList<>();

        try {
            NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(bytes), false);
            Tag tag = nbtInputStream.readTag();
            if (tag.getType() == TagType.TAG_COMPOUND && tag.getValue() instanceof CompoundMap) {
                CompoundMap root = (CompoundMap) tag.getValue();
                if (root.containsKey("StoredEnchantments")) {
                    List<CompoundTag> enchants = (List<CompoundTag>) root.get("StoredEnchantments").getValue();
                    for (CompoundTag enchant : enchants) {
                        enchList.add(Collections.singletonMap(Enchantments_1_8.getFromId((Short) enchant.getValue().get("id").getValue()).name(), (Short) enchant.getValue().get("lvl").getValue()));
                    }
                } else if (root.containsKey("ench")) {
                    List<CompoundTag> enchants = (List<CompoundTag>) root.get("ench").getValue();
                    for (CompoundTag enchant : enchants) {
                        enchList.add(Collections.singletonMap(Enchantments_1_8.getFromId((Short) enchant.getValue().get("id").getValue()).name(), (Short) enchant.getValue().get("lvl").getValue()));
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return enchList;
    }
}
