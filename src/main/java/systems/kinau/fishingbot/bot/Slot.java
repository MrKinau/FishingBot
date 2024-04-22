package systems.kinau.fishingbot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import systems.kinau.fishingbot.bot.item.ItemData;

@Getter
@AllArgsConstructor
@ToString
public class Slot {

    public static final Slot EMPTY = new Slot(false, -1, (byte) -1, -1, null);

    private boolean present;
    private int itemId;
    private byte itemCount;
    private int itemDamage;
    private ItemData itemData;

    public void writeItemData(ByteArrayDataOutput output, int protocolId) {
        if (itemData == null) return;
        itemData.write(output, protocolId);
    }
}
