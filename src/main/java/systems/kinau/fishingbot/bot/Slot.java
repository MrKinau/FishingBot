package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Slot {

    public static final Slot EMPTY = new Slot(false, -1, (byte) -1, (short) -1, new byte[0]);

    @Getter private boolean present;
    @Getter private int itemId;
    @Getter private byte itemCount;
    @Getter private short itemDamage;
    @Getter private byte[] nbtData;
}
