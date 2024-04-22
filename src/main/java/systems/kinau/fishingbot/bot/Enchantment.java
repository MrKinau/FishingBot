package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Enchantment {

    private String enchantmentType;
    private int level;

    public String getCleanEnchantmentName() {
        return enchantmentType.replace("minecraft:", "");
    }
}
