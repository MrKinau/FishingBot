package systems.kinau.fishingbot.utils;

import javafx.scene.image.Image;
import systems.kinau.fishingbot.Main;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.loot.LootItem;

import java.util.List;
import java.util.Objects;

public class ImageUtils {

    public static String getItemURL(String filename) {
        return String.format("https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/items/%s", filename.toLowerCase()).replace(" ", "%20");
    }

    public static String getItemURL(Item item) {
        String fileType = (item.getEnchantments() == null || item.getEnchantments().isEmpty()) ? ".png" : ".gif";
        return getItemURL(item.getName() + fileType);
    }

    public static Image getImage(String filename) {
        return new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("img/items/" + filename.toLowerCase())));
    }

    public static Image getImage(LootItem item) {
        String fileType = (item.getEnchantments() == null || item.getEnchantments().isEmpty()) ? ".png" : ".gif";
        return getImage(item.getName() + fileType);
    }

    public static String getFileName(String itemName, List<Enchantment> enchantments) {
        String fileType = (enchantments == null || enchantments.isEmpty()) ? ".png" : ".gif";
        return itemName + fileType;
    }
}
