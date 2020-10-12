package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Slot {

    @Getter private boolean present;
    @Getter private int itemId;
    @Getter private byte itemCount;
    @Getter private short itemDamage;
    @Getter private byte[] nbtData;
}
