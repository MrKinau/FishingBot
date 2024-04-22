package systems.kinau.fishingbot.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class EnchantmentWithCount {

    private String name;
    private int level;
    private int count;

}
