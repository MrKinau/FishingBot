package systems.kinau.fishingbot.network.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 05.11.2020
 *
 * @author Summerfeeling
 */
public class StringUtils {

    public static String[] splitDescription(String desc) {
        // Final list for lore array
        List<String> list = new ArrayList<>();

        // Split every line break
        for (String lineBreak : desc.split("\n")) {
            // Final line
            StringBuilder builder = new StringBuilder();

            // Split every whitespace
            for (String word : lineBreak.split(" ")) {
                if (builder.length() + word.trim().length() >= 100) {
                    list.add(builder.toString().trim()); // Add to list
                    builder = new StringBuilder(); // Reset
                }

                builder.append(word).append(" ");
            }

            // Add rest
            list.add(builder.toString().trim());
        }

        // Return converted to array
        return list.toArray(new String[0]);
    }

}
