package systems.kinau.fishingbot.gui;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class LootItem {

    private String name;
    private int count;
    private List<Map<String, Short>> enchantments;
}
