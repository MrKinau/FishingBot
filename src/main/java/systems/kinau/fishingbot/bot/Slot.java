package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import systems.kinau.fishingbot.utils.nbt.NBTTag;

@AllArgsConstructor
@ToString
public class Slot {

    public static final Slot EMPTY = new Slot(false, -1, (byte) -1, -1, null);

    @Getter private boolean present;
    @Getter private int itemId;
    @Getter private byte itemCount;
    @Getter private int itemDamage;
    @Getter private NBTTag nbtData;
}
