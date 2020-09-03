package systems.kinau.fishingbot.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Enchantment {

    @Getter private String name;
    @Getter private short level;
    @Getter private int count;

}
