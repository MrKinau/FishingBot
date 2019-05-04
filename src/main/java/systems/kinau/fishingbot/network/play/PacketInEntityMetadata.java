/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.play;

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
import systems.kinau.fishingbot.network.NetworkHandler;
import systems.kinau.fishingbot.network.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class PacketInEntityMetadata extends Packet {

    private static final List<Integer> FISH_IDS = Arrays.asList(625, 626, 627, 628);

    @Override
    public void write(ByteArrayDataOutput out) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length) {
        if(!networkHandler.getFishingManager().isTrackingNextEntityMeta())
            return;
        readVarInt(in);
        readWatchableObjects(in, networkHandler);
    }

    private void readWatchableObjects(ByteArrayDataInputWrapper in, NetworkHandler networkHandler) {
        while(true) {
            if (in.getAvailable() == 0)
                break;
            int var2 = in.readByte();
            if(var2 == -1)
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

                        String name = ItemHandler.getItemName(itemID).replace("minecraft:", "");
                        FishingBot.getLog().info("Caught \"" + name + "\"");

                        if(FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if(FishingBot.getConfig().getAnnounceType() == AnnounceType.ALL_BUT_FISH && !FISH_IDS.contains(itemID))
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if (enchantments.isEmpty())
                            break;

                        if(FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_ENCHANTED)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));
                        else if(FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID == 779)
                            networkHandler.sendPacket(new PacketOutChat(Constants.PREFIX + "Caught: \"" + name + "\""));

                        if(FishingBot.getConfig().getAnnounceType() == AnnounceType.ONLY_BOOKS && itemID != 779)
                            break;

                        Thread.sleep(200);

                        if (!enchantments.isEmpty()) {
                            for (Pair<String, Short> enchantment : enchantments) {
                                networkHandler.sendPacket(new PacketOutChat("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue())));
                                FishingBot.getLog().info("-> " + enchantment.getKey().replace("minecraft:", "").toUpperCase() + " " + getRomanLevel(enchantment.getValue()));
                            }
                        }

                        break;
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
            } catch(Exception ex) {
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
            if(tag.getType() == TagType.TAG_COMPOUND) {
                if(tag.getValue() instanceof CompoundMap) {
                    CompoundMap root = (CompoundMap) tag.getValue();
                    if(root.containsKey("StoredEnchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("StoredEnchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(new Pair<>((String) enchant.getValue().get("id").getValue(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    } else if(root.containsKey("Enchantments")) {
                        List<CompoundTag> enchants = (List<CompoundTag>) root.get("Enchantments").getValue();
                        for (CompoundTag enchant : enchants) {
                            enchList.add(new Pair<>((String) enchant.getValue().get("id").getValue(), (Short) enchant.getValue().get("lvl").getValue()));
                        }
                    }
                }
            }
        } catch (IOException ignored) { }
        return enchList;
    }

    private String getRomanLevel(int number) {
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return "" + number;
        }
    }
}
