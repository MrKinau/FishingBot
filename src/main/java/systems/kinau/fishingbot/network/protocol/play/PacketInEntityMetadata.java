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
import javafx.util.Pair;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.fishing.ItemHandler;
import systems.kinau.fishingbot.io.Constants;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;
import systems.kinau.fishingbot.network.utils.Enchantments_1_8;
import systems.kinau.fishingbot.network.utils.Material_1_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class PacketInEntityMetadata extends Packet {

    private static final List<Integer> FISH_IDS_1_14 = Arrays.asList(625, 626, 627, 628);
    private static final List<Integer> FISH_IDS_1_8 = Collections.singletonList(349);

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if (!networkHandler.getFishingManager().isTrackingNextEntityMeta())
            return;
        readVarInt(in);
        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_8: {
                readWatchableObjects_1_8(in, networkHandler);
                break;
            }
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
                readWatchableObjects_1_9(in, networkHandler);
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13: {
                readWatchableObjects_1_13(in, networkHandler);
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_14: {
                readWatchableObjects_1_14(in, networkHandler);
                break;
            }
        }
    }

    //TODO: Dont mess it so hard up

    private void readWatchableObjects_1_14(ByteArrayDataInputWrapper in, NetworkHandler networkHandler) {
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
                        in.readFloat();
                        break;
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
                        if (in.readBoolean()) {
                            //Chat is missing
                        }
                        break;
                    }
                    case 6: {
                        boolean present = in.readBoolean();
                        if (!present)
                            break;
                        networkHandler.getFishingManager().setTrackingNextEntityMeta(false);
                        int itemID = readVarInt(in);
                        byte count = in.readByte();
                        List<Pair<String, Short>> enchantments = readNBT(in);

                        String name = ItemHandler.getItemName(itemID, FishingBot.getServerProtocol()).replace("minecraft:", "");
                        FishingBot.getLog().info("Caught \"" + name + "\"");

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.NONE)
                            return;
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_14.contains(itemID))
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (enchantments.isEmpty())
                            return;

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_ENCHANTED)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID == 779)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID != 779)
                            return;

                        Thread.sleep(200);

                        if (!enchantments.isEmpty()) {
                            for (Pair<String, Short> enchantment : enchantments) {
                                networkHandler.sendPacket(new PacketOutChat("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue())));
                                FishingBot.getLog().info("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue()));
                            }
                        }

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
                break;
            }
        }
    }

    private void readWatchableObjects_1_13(ByteArrayDataInputWrapper in, NetworkHandler networkHandler) {
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
                        in.readFloat();
                        break;
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
                        if (in.readBoolean()) {
                            //Chat is missing
                        }
                        break;
                    }
                    case 6: {
                        networkHandler.getFishingManager().setTrackingNextEntityMeta(false);
                        int itemID = in.readShort();
                        byte count = in.readByte();
                        List<Pair<String, Short>> enchantments = readNBT(in);

                        String name = ItemHandler.getItemName(itemID, FishingBot.getServerProtocol()).replace("minecraft:", "");
                        FishingBot.getLog().info("Caught \"" + name + "\"");

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.NONE)
                            return;
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_14.contains(itemID))
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (enchantments.isEmpty())
                            return;

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_ENCHANTED)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID == 779)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID != 779)
                            return;

                        Thread.sleep(200);

                        if (!enchantments.isEmpty()) {
                            for (Pair<String, Short> enchantment : enchantments) {
                                networkHandler.sendPacket(new PacketOutChat("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue())));
                                FishingBot.getLog().info("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue()));
                            }
                        }

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
                break;
            }
        }
    }

    private void readWatchableObjects_1_9(ByteArrayDataInputWrapper in, NetworkHandler networkHandler) {
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
                        in.readFloat();
                        break;
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
                        networkHandler.getFishingManager().setTrackingNextEntityMeta(false);
                        int itemID = in.readShort();
                        byte count = in.readByte();
                        short damage = in.readShort();
                        String name = Material_1_8.getMaterial(itemID).name();
                        FishingBot.getLog().info("Caught \"" + name + "\"");
                        List<Pair<String, Short>> enchantments = readNBT_1_8(in);

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.NONE)
                            return;
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_8.contains(itemID))
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (enchantments.isEmpty())
                            return;

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_ENCHANTED)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID == 403)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID != 403)
                            return;

                        Thread.sleep(200);

                        if (!enchantments.isEmpty()) {
                            for (Pair<String, Short> enchantment : enchantments) {
                                networkHandler.sendPacket(new PacketOutChat("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue())));
                                FishingBot.getLog().info("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue()));
                            }
                        }

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
                    case 13: {
                        return;
                    }
                }
            } catch (Exception ex) {
                break;
            }
        }
    }

    private void readWatchableObjects_1_8(ByteArrayDataInputWrapper in, NetworkHandler networkHandler) {
        while (true) {
            if (in.getAvailable() == 0)
                break;
            byte var2 = in.readByte();
            if (var2 == 0x7F)
                break;

            int i = (var2 & 224) >> 5;
            int j = var2 & 31;

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
                        in.readFloat();
                        break;
                    }
                    case 4: {
                        readString(in);
                        break;
                    }

                    case 5: {
                        networkHandler.getFishingManager().setTrackingNextEntityMeta(false);
                        int itemID = in.readShort();
                        byte count = in.readByte();
                        short damage = in.readShort();
                        String name = Material_1_8.getMaterial(itemID).name();
                        FishingBot.getLog().info("Caught \"" + name + "\"");
                        List<Pair<String, Short>> enchantments = readNBT_1_8(in);

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.NONE)
                            return;
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_8.contains(itemID))
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (enchantments.isEmpty())
                            return;

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_ENCHANTED)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID == 403)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID != 403)
                            return;

                        Thread.sleep(200);

                        if (!enchantments.isEmpty()) {
                            for (Pair<String, Short> enchantment : enchantments) {
                                networkHandler.sendPacket(new PacketOutChat("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue())));
                                FishingBot.getLog().info("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue()));
                            }
                        }

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

    private List<Pair<String, Short>> readNBT(ByteArrayDataInputWrapper in) {

        byte[] bytes = new byte[in.getAvailable()];
        in.readFully(bytes);

        List<Pair<String, Short>> enchList = new ArrayList<>();

        try {
            NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(bytes), false);
            Tag tag = nbtInputStream.readTag();
            if (tag.getType() == TagType.TAG_COMPOUND) {
                if (tag.getValue() instanceof CompoundMap) {
                    CompoundMap root = (CompoundMap) tag.getValue();
                    if (root.containsKey("StoredEnchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("StoredEnchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(new Pair<>((String) enchant.getValue().get("id").getValue(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    } else if (root.containsKey("Enchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("Enchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(new Pair<>((String) enchant.getValue().get("id").getValue(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return enchList;
    }

    private List<Pair<String, Short>> readNBT_1_8(ByteArrayDataInputWrapper in) {

        byte[] bytes = new byte[in.getAvailable()];
        in.readFully(bytes);

        List<Pair<String, Short>> enchList = new ArrayList<>();

        try {
            NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(bytes), false);
            Tag tag = nbtInputStream.readTag();
            if (tag.getType() == TagType.TAG_COMPOUND) {
                if (tag.getValue() instanceof CompoundMap) {
                    CompoundMap root = (CompoundMap) tag.getValue();
                    if (root.containsKey("StoredEnchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("StoredEnchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(new Pair<>(Enchantments_1_8.getFromId((Short) enchant.getValue().get("id").getValue()).name(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    } else if (root.containsKey("ench")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("ench").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(new Pair<>(Enchantments_1_8.getFromId((Short) enchant.getValue().get("id").getValue()).name(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    }
                }
            }
        } catch (IOException ignored) { }
        return enchList;
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
}
