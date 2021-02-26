package systems.kinau.fishingbot.utils;

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

    public static String getRomanLevel(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "" + number;
        }
    }

    public static String maskUsername(String loginName) {
        return loginName.contains("@") ? loginName.split("@")[0].replaceAll(".", "*") + "@" + loginName.split("@")[1] : loginName;
    }

}
