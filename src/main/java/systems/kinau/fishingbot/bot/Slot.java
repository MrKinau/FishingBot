package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Slot {

    @Getter private boolean present;
    @Getter private int itemId;
    @Getter private byte itemCount;
    @Getter private short itemDamage;
    @Getter private byte[] nbtData;
}
