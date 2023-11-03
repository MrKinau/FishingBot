package systems.kinau.fishingbot.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UUIDUtils {

    public static UUID createOfflineUUID(String playerName) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
    }

    public static String createOfflineUUIDString(String playerName) {
        return createOfflineUUID(playerName).toString();
    }

    public static String withoutDashes(UUID uuid) {
        return withoutDashes(uuid.toString());
    }

    public static String withoutDashes(String uuid) {
        if (uuid == null) return null;
        return uuid.replace("-", "");
    }

    public static String withDashes(String uuid) {
        if (uuid == null) return null;
        return uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
        );
    }


}
