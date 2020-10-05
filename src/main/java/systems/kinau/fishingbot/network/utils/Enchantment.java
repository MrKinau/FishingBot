package systems.kinau.fishingbot.network.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Enchantment {

    private EnchantmentType enchantmentType;
    private short level;
}
