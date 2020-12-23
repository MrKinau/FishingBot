package systems.kinau.fishingbot.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.enums.EnchantmentType;

@AllArgsConstructor
@Data
public class Enchantment {

    private EnchantmentType enchantmentType;
    private short level;
}
