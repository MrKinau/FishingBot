package systems.kinau.fishingbot.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import systems.kinau.fishingbot.bot.Enchantment;

import java.util.List;

@Data
@AllArgsConstructor
public class LootItem {

    private String name;
    private int count;
    private List<Enchantment> enchantments;
}
